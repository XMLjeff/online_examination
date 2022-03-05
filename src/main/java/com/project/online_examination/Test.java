package com.project.online_examination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：xmljeff
 * @date ：Created in 2022/1/17 20:48
 * @description：
 * @modified By：
 * @version: $
 */
public class Test {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>() ;
        list.add("1");

        System.out.println(list.contains(1+""));
    }
}
