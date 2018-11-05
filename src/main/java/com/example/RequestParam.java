package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jason Xiao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestParam {

    private Long id;
    private String name;
}
