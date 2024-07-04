package com.dedication.force.common;

public record HttpResponse<T>(Integer code, String message, T data) { }
