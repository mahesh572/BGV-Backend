package com.org.bgv.vendor.builder;

import java.util.List;

import com.org.bgv.vendor.dto.ObjectFieldDTO;

public interface ObjectFieldBuilder<T> {
    List<ObjectFieldDTO> buildFields(T source);
}

