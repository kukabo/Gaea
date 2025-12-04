package main

import "fmt"

// Calculator结构体 - 只读取，用值接收者
type Calculator struct {
	num1 float64
	num2 float64
}

// MathHelper结构体 - 需要修改，用指针接收者
type MathHelper struct {
	history []float64
}

// 值接收者：只读取数据，不需要修改原对象
func (c Calculator) Add() float64 {
	result := c.num1 + c.num2
	fmt.Printf("Calculator: %.2f + %.2f = %.2f\n", c.num1, c.num2, result)
	return result
}

// 指针接收者：需要修改原对象的history字段
func (m *MathHelper) RecordCalculation(calc Calculator) {
	sum := calc.Add()                  // 调用Calculator的方法
	m.history = append(m.history, sum) // 修改原对象的history
	fmt.Printf("MathHelper: 已记录计算结果 %.2f 到历史记录\n", sum)
}

// 值接收者：只读取history，不需要修改
func (m MathHelper) ShowHistory() {
	fmt.Printf("MathHelper: 历史记录中共有 %d 个计算结果\n", len(m.history))
	for i, value := range m.history {
		fmt.Printf("  第%d个结果: %.2f\n", i+1, value)
	}
}

// 演示函数：展示值接收者vs指针接收者的区别
func demonstratePointerDifference() {
	fmt.Println("\n=== 指针 vs 值 接收者演示 ===")

	// 值接收者的例子（Calculator）
	calc := Calculator{num1: 10, num2: 20}
	fmt.Printf("原始Calculator: %+v\n", calc)
	calc.Add()
	fmt.Printf("调用Add()后Calculator: %+v (值接收者不会修改原对象)\n", calc)

	// 指针接收者的例子（MathHelper）
	helper := MathHelper{history: make([]float64, 0)}
	fmt.Printf("原始MathHelper: history长度=%d\n", len(helper.history))
	helper.RecordCalculation(calc) // 这里会修改helper.history
	fmt.Printf("调用RecordCalculation()后MathHelper: history长度=%d (指针接收者修改了原对象)\n", len(helper.history))
}

// 构造函数
func NewCalculator(num1, num2 float64) Calculator {
	return Calculator{num1: num1, num2: num2}
}

func NewMathHelper() MathHelper {
	return MathHelper{history: make([]float64, 0)}
}

// 主函数
func main() {
	// 变量声明
	var firstNumber float64 = 15.5
	var secondNumber float64 = 25.3

	// 创建实例
	calculator := NewCalculator(firstNumber, secondNumber)
	helper := NewMathHelper()

	fmt.Println("=== Go程序：两个数字相加求和 ===")
	fmt.Printf("输入的数字: %.2f 和 %.2f\n\n", firstNumber, secondNumber)

	// 结构体之间的互相调用
	fmt.Println("3. 开始计算（Calculator调用）...")
	calculator.Add()

	fmt.Println("\n4. 记录到历史（MathHelper调用Calculator）...")
	helper.RecordCalculation(calculator)

	fmt.Println("\n5. 再次计算并记录...")
	anotherCalc := NewCalculator(10.0, 20.0)
	helper.RecordCalculation(anotherCalc)

	fmt.Println("\n6. 显示历史记录...")
	helper.ShowHistory()

	// 最终结果
	finalResult := firstNumber + secondNumber
	fmt.Printf("\n7. 最终结果验证: %.2f + %.2f = %.2f\n", firstNumber, secondNumber, finalResult)

	// 演示指针的区别
	demonstratePointerDifference()

	fmt.Println("\n=== 程序运行完成 ===")
}
