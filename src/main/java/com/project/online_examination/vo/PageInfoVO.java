package com.project.online_examination.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: xiahui
 * @date: Created in 2020/6/23 15:26
 * @description: 分页信息
 * @version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageInfoVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long total;
    private List<T> data;
}