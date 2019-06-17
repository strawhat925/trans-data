package com.warehouse.data;

/**
 * ${DESCRIPTION}
 *
 * @author zli
 * @create 2019-06-01 11:28
 **/
public class MathTest {

    public static void main(String[] args) {
        /**
         * Math.sqrt() 计算平方根 如果一个数的平方等于a,那么这个数就是a的平方根,也叫做a的二次方根 例如:5X5=25, 5就是25的平方根
         * Math.cbrt() 计算立方根 x³=a  x为a的立方根
         * Math.pow(a,b) 计算a的b次方
         * Math.max() 计算最大值
         * Math.min() 计算最小值
         */
        System.out.println(Math.sqrt(16));  //4.0
        System.out.println(Math.cbrt(8));   //2.0
        System.out.println(Math.pow(3, 2)); //9.0
        System.out.println(Math.max(2, 3)); //3
        System.out.println(Math.min(2, 3)); //2

        /**
         * Math.abs() 求绝对值
         */
        System.out.println(Math.abs(-10.6)); //10.6
        System.out.println(Math.abs(10.1));  //10.1

        /**
         * ceil天花板的意思，返回最大值
         */
        System.out.println(Math.ceil(-10.1));   //-10.0
        System.out.println(Math.ceil(10.7));    //11.0

        /**
         * floor地板的意思，返回最小值
         */
        System.out.println(Math.floor(-10.1));  //-11.0
        System.out.println(Math.floor(10.7));   //10.0


        /**
         * Math.random() 取得一个大于或者等于0.0小于不等于1.0的随机数
         */
        System.out.println(Math.random());

        /**
         * Math.rint() 四舍五入
         * .5只有基数时，四舍五入
         */
        System.out.println(Math.rint(10.1));    //10.0
        System.out.println(Math.rint(11.5));    //12.0
        System.out.println(Math.rint(12.5));    //12.0


        /**
         * Math.round() 四舍五入
         */
        System.out.println(Math.round(10.1));   //10
        System.out.println(Math.round(10.5));   //11
    }
}
