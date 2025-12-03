package Ai;

import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HunYuanChatExample {

    public static final Logger logger = LoggerFactory.getLogger(HunYuanChatExample.class);
    private static final String API_URL = "http://hunyuanapi.woa.com/openapi/v1/chat/completions";
    private static final String API_KEY = "";
    public static final String MODEL_ID = "hunyuan-turbos-latest";
    public static final HashMap<String, McpBean> mcpServiceMap = new HashMap<>();

    public static void main(String[] args) {
        // 初始化MCP服务
        initializeMcpServices();
        
        // 执行MCP ReAct流程并输出结果
        String userMessage = "深圳今天天气怎么样？";
        String finalAnswer = callHunYuanAnalyze(userMessage);
        
        System.out.println("=== 最终回答 ===");
        System.out.println(finalAnswer);
    }
    
    /**
     * 初始化MCP服务
     */
    private static void initializeMcpServices() {
        // 天气查询服务
        McpBean weatherService = new McpBean();
        weatherService.setName("weather");
        weatherService.setMethod("findLocationWeather");
        weatherService.setDescription("查询指定城市的天气信息");
        weatherService.setParamTypes(new String[]{"java.lang.String"});
        weatherService.setParamNames(new String[]{"location"});
        mcpServiceMap.put("weather", weatherService);
        
        logger.info("MCP services initialized: {}", mcpServiceMap.keySet());
    }



    private static String buildMcpServicesInfo() {
        StringBuilder mcpServicesInfo = new StringBuilder("可用的MCP服务：\n");
        for (Map.Entry<String, McpBean> entry : mcpServiceMap.entrySet()) {
            McpBean service = entry.getValue();
            mcpServicesInfo.append("- ").append(service.getName())
                    .append("：").append(service.getDescription())
                    .append("（方法：").append(service.getMethod()).append("）\n");
            
            // 动态显示参数信息
            if (service.getParamNames() != null && service.getParamTypes() != null) {
                for (int i = 0; i < service.getParamNames().length; i++) {
                    String paramName = service.getParamNames()[i];
                    String paramType = service.getParamTypes()[i];
                    String paramDesc = getParameterDescription(paramName, paramType);
                    mcpServicesInfo.append("  入参：").append(paramName)
                            .append("（").append(paramDesc).append("）\n");
                }
            }
        }
        return mcpServicesInfo.toString();
    }
    
    /**
     * 获取参数描述
     */
    private static String getParameterDescription(String paramName, String paramType) {
        switch (paramName) {
            case "location":
                return "城市名称，" + paramType;
            default:
                return paramType;
        }
    }

    /**
     * 第一步：将用户请求和MCP服务信息发送给大模型
     * @param userMessage 用户请求
     * @return 大模型返回的Thought和Action
     */
    private static JSONObject step1_SendUserRequestToLLM(String userMessage) {
        // 构建MCP服务信息
        String mcpServicesInfo = buildMcpServicesInfo();
        
        // 构建发送给大模型的完整信息
        String conversation = "你是一个智能助手，需要分析用户请求并决定是否调用MCP服务。\n\n" +
                "可用的MCP服务：\n" + mcpServicesInfo + "\n" +
                "用户请求：" + userMessage + "\n\n" +
                "请按照ReAct模式进行思考，并按以下格式输出：\n" +
                "Thought: [你的思考过程，分析用户需求，判断是否需要调用MCP服务]\n" +
                "Action: [如果需要调用服务，输出JSON格式：{\"mcpService\": \"服务名\", \"params\": {\"参数名\": \"参数值\"}, \"paramTypes\": {\"参数名\": \"参数类型\"}}；如果直接回答，输出回答内容]\n\n" +
                "注意：\n" +
                "1. params中的参数值必须符合paramTypes中指定的类型\n" +
                "2. paramTypes中的类型必须是完整的Java类名（如：java.lang.String, java.lang.Integer等）\n" +
                "3. 请根据上面MCP服务信息中的参数类型来填写paramTypes\n";

        // 发送给大模型
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", conversation);

        JSONObject data = new JSONObject();
        data.put("model", MODEL_ID);
        data.put("messages", new JSONObject[]{message});

        Request request = constructRequest(data);
        String responseBody = callHunYuanChatApi(request);

        // 解析大模型返回结果
        return parseLLMResponse(responseBody);
    }
    
    /**
     * 第二步：解析大模型返回的Thought和Action
     * @param responseBody 大模型响应
     * @return 解析后的结果
     */
    private static JSONObject parseLLMResponse(String responseBody) {
        JSONObject response = new JSONObject(responseBody);
        
        if (!response.has("choices") || response.getJSONArray("choices").length() == 0) {
            logger.error("Invalid response: missing choices array");
            return new JSONObject();
        }
        
        JSONObject choice = response.getJSONArray("choices").getJSONObject(0);
        if (!choice.has("message") || !choice.getJSONObject("message").has("content")) {
            logger.error("Invalid response: missing message content");
            return new JSONObject();
        }
        
        String contentStr = choice.getJSONObject("message").getString("content");
        logger.info("LLM Response: {}", contentStr);
        
        // 解析ReAct格式输出
        JSONObject result = new JSONObject();
        String[] lines = contentStr.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Thought:")) {
                result.put("thought", line.substring(8).trim());
            } else if (line.startsWith("Action:")) {
                String actionStr = line.substring(7).trim();
                try {
                    // 尝试解析为JSON（MCP服务调用）
                    JSONObject actionJson = new JSONObject(actionStr);
                    result.put("action", actionJson);
                    result.put("isMcpCall", true);
                } catch (Exception e) {
                    // 如果不是JSON，作为直接回答处理
                    result.put("action", actionStr);
                    result.put("isMcpCall", false);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 第三步：通过反射调用MCP服务方法
     * @param serviceName 服务名称
     * @param params 参数
     * @param paramTypes 参数类型
     * @return MCP服务调用结果
     */
    private static String step3_CallMcpServer(String serviceName, JSONObject params, JSONObject paramTypes) {
        return callMcpServer(serviceName, params, paramTypes);
    }
    
    /**
     * 第四步：将MCP服务结果返回给大模型获取最终回答
     * @param originalThought 原始思考过程
     * @param serviceResult MCP服务结果
     * @return 最终回答
     */
    private static String step4_SendResultToLLM(String originalThought, String serviceResult) {
        String conversation = "基于以下信息提供最终回答：\n\n" +
                "原始思考过程：" + originalThought + "\n" +
                "MCP服务调用结果：" + serviceResult + "\n\n" +
                "请基于MCP服务的结果，给用户一个完整、准确的回答。";
        
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", conversation);

        JSONObject data = new JSONObject();
        data.put("model", MODEL_ID);
        data.put("messages", new JSONObject[]{message});

        Request request = constructRequest(data);
        String responseBody = callHunYuanChatApi(request);
        
        // 解析最终回答
        try {
            JSONObject response = new JSONObject(responseBody);
            if (response.has("choices") && response.getJSONArray("choices").length() > 0) {
                JSONObject choice = response.getJSONArray("choices").getJSONObject(0);
                if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                    return choice.getJSONObject("message").getString("content");
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing final response", e);
        }
        
        return "抱歉，无法生成最终回答。";
    }
    
    /**
     * 完整的MCP ReAct流程
     * @param userMessage 用户请求
     * @return 最终回答
     */
    private static String callHunYuanAnalyze(String userMessage) {
        logger.info("=== 开始MCP ReAct流程 ===");
        logger.info("用户请求: {}", userMessage);
        
        // 第一步：发送用户请求和MCP服务信息给大模型
        logger.info("第一步：发送用户请求和MCP服务信息给大模型");
        JSONObject llmResponse = step1_SendUserRequestToLLM(userMessage);
        
        String thought = llmResponse.optString("thought", "");
        boolean isMcpCall = llmResponse.optBoolean("isMcpCall", false);
        Object action = llmResponse.opt("action");
        
        logger.info("大模型思考过程: {}", thought);
        logger.info("是否需要调用MCP服务: {}", isMcpCall);
        
        if (!isMcpCall) {
            // 大模型直接回答，无需调用MCP服务
            logger.info("大模型直接回答: {}", action.toString());
            return action.toString();
        }
        
        // 第二步：解析大模型返回的Action（已在parseLLMResponse中完成）
        JSONObject actionJson = (JSONObject) action;
        String serviceName = actionJson.optString("mcpService");
        JSONObject params = actionJson.optJSONObject("params");
        JSONObject paramTypes = actionJson.optJSONObject("paramTypes");
        
        logger.info("第二步：大模型决定调用MCP服务: {}, 参数: {}, 参数类型: {}", serviceName, params, paramTypes);
        
        // 第三步：通过反射调用MCP服务方法
        logger.info("第三步：通过反射调用MCP服务方法");
        String serviceResult = step3_CallMcpServer(serviceName, params, paramTypes);
        logger.info("MCP服务调用结果: {}", serviceResult);
        
        // 第四步：将结果返回给大模型获取最终回答
        logger.info("第四步：将MCP服务结果返回给大模型获取最终回答");
        String finalAnswer = step4_SendResultToLLM(thought, serviceResult);
        
        logger.info("=== MCP ReAct流程完成 ===");
        logger.info("最终回答: {}", finalAnswer);
        
        // 构建返回结果（保持原有格式兼容性）
        JSONObject result = new JSONObject();
        result.put("thought", thought);
        result.put("action", actionJson);
        result.put("observation", serviceResult);
        result.put("finalAnswer", finalAnswer);
        
        return result.toString();
    }

    

    

    private static Request constructRequest(JSONObject data) {
        // 构建请求头
        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer " + API_KEY)
                .add("Content-Type", "application/json")
                .build();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                data.toString()
        );

        // 构建请求
        Request request = new Request.Builder()
                .url(API_URL)
                .headers(headers)
                .post(body)
                .build();

        // 打印请求头（格式化）
        logger.info("Request Headers:\n{}", request.headers().toString().replace(", ", "\n"));
        // 打印请求体（格式化 JSON）
        logger.info("Request Body (Formatted JSON):\n{}", new JSONObject(data.toString()).toString(4));

        return request;
    }

    private static String callHunYuanChatApi(Request request) {
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            // 打印状态码
            logger.info("Status Code: {}", response.code());

            // 打印响应头
            logger.info("Response Headers: {}", response.headers());

            // 解析并格式化 JSON 响应体
            String responseBody = response.body().string();
            try {
                JSONObject jsonResponse = new JSONObject(responseBody);
                logger.info("Response Body (Formatted JSON):\n{}", jsonResponse.toString(4));
                return responseBody;
            } catch (Exception e) {
                logger.info("Response Body (Raw):\n{}", responseBody);
                return responseBody;
            }
        } catch (IOException e) {
            logger.error("Request failed");
            throw new RuntimeException(e);
        }
    }

    //mcp 方法
    private static String findLocationWeather(String location) {
        //天气数据
        HashMap<String, String> mockData = new HashMap<>();
        mockData.put("深圳", "晴朗，气温185度");
        mockData.put("北京", "多云");
        mockData.put("上海", "阴");
        mockData.put("广州", "晴朗");
        return mockData.get(location);
    }

    /**
     * 调用MCP服务
     * @param serviceName 服务名称
     * @param params 参数
     * @param paramTypesFromLLM 大模型返回的参数类型（可选）
     * @return 服务调用结果
     */
    private static String callMcpServer(String serviceName, JSONObject params, JSONObject paramTypesFromLLM) {
        try {
            // 检查服务是否存在
            if (!mcpServiceMap.containsKey(serviceName)) {
                logger.error("MCP service not found: {}", serviceName);
                return "错误：未找到MCP服务 " + serviceName;
            }
            
            McpBean service = mcpServiceMap.get(serviceName);
            logger.info("Calling MCP service: {} with params: {}, LLM paramTypes: {}", serviceName, params, paramTypesFromLLM);
            
            // 通过反射调用MCP服务方法
            String methodName = service.getMethod();
            String[] paramNames = service.getParamNames();
            
            // 优先使用大模型返回的参数类型，否则使用预定义类型
            String[] paramTypes;
            if (paramTypesFromLLM != null && paramTypesFromLLM.length() > 0) {
                // 从大模型返回的paramTypes构建数组
                paramTypes = new String[paramNames.length];
                for (int i = 0; i < paramNames.length; i++) {
                    String paramName = paramNames[i];
                    paramTypes[i] = paramTypesFromLLM.optString(paramName, service.getParamTypes()[i]);
                }
                logger.info("Using LLM provided paramTypes: {}", (Object) paramTypes);
            } else {
                // 使用预定义的参数类型
                paramTypes = service.getParamTypes();
                logger.info("Using predefined paramTypes: {}", (Object) paramTypes);
            }
            
            // 构建参数类型数组
            Class<?>[] paramClasses = new Class<?>[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramClasses[i] = getClassForName(paramTypes[i]);
            }
            
            Method method = HunYuanChatExample.class.getDeclaredMethod(methodName, paramClasses);
            method.setAccessible(true);
            
            // 构建参数值数组
            Object[] paramValues = new Object[paramNames.length];
            for (int i = 0; i < paramNames.length; i++) {
                String paramName = paramNames[i];
                String paramType = paramTypes[i];
                paramValues[i] = convertParamValue(params, paramName, paramType);
            }
            
            // 调用方法
            Object result = method.invoke(null, paramValues);
            
            logger.info("MCP service result: {}", result);
            return result != null ? result.toString() : "null";
            
        } catch (Exception e) {
            logger.error("Error calling MCP service: " + serviceName, e);
            return "错误：调用MCP服务失败 - " + e.getMessage();
        }
    }
    
    /**
     * 根据类型名称获取Class对象
     */
    private static Class<?> getClassForName(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "java.lang.String":
                return String.class;
            case "int":
            case "java.lang.Integer":
                return int.class;
            case "long":
            case "java.lang.Long":
                return long.class;
            case "double":
            case "java.lang.Double":
                return double.class;
            case "boolean":
            case "java.lang.Boolean":
                return boolean.class;
            default:
                return Class.forName(typeName);
        }
    }
    
    /**
     * 转换参数值
     */
    private static Object convertParamValue(JSONObject params, String paramName, String paramType) {
        if (params == null || !params.has(paramName)) {
            return getDefaultValue(paramType);
        }
        
        Object value = params.get(paramName);
        
        try {
            switch (paramType) {
                case "java.lang.String":
                    return value.toString();
                case "int":
                case "java.lang.Integer":
                    return Integer.parseInt(value.toString());
                case "long":
                case "java.lang.Long":
                    return Long.parseLong(value.toString());
                case "double":
                case "java.lang.Double":
                    return Double.parseDouble(value.toString());
                case "boolean":
                case "java.lang.Boolean":
                    return Boolean.parseBoolean(value.toString());
                default:
                    return value;
            }
        } catch (Exception e) {
            logger.warn("Failed to convert parameter {} to type {}, using default value", paramName, paramType, e);
            return getDefaultValue(paramType);
        }
    }
    
    /**
     * 获取类型的默认值
     */
    private static Object getDefaultValue(String paramType) {
        switch (paramType) {
            case "java.lang.String":
                return "";
            case "int":
            case "java.lang.Integer":
                return 0;
            case "long":
            case "java.lang.Long":
                return 0L;
            case "double":
            case "java.lang.Double":
                return 0.0;
            case "boolean":
            case "java.lang.Boolean":
                return false;
            default:
                return null;
        }
    }

}

class McpBean {
    private String name; // mcp服务名称
    private String method; // mcp服务方法
    private String description; // mcp服务描述
    private String[] paramTypes; // 参数类型数组
    private String[] paramNames; // 参数名称数组

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(String[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public void setParamNames(String[] paramNames) {
        this.paramNames = paramNames;
    }
}