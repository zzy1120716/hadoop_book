package cn.itcast.bigdata.mr.flowsum;

import java.util.ArrayList;

public class Test {

    public static void main(String[] args) {

        ArrayList<FlowBean> arrayList = new ArrayList<FlowBean>();

        FlowBean bean = new FlowBean();

        bean.set(1, 2);

        arrayList.add(bean);

        bean.set(10, 20);

        arrayList.add(bean);

        bean.set(100, 200);

        arrayList.add(bean);

        System.out.println(arrayList.size());

        for (FlowBean b : arrayList) {

            System.out.println(b);

        }
    }

}
