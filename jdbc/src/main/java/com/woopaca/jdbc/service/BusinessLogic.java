package com.woopaca.jdbc.service;

@FunctionalInterface
interface BusinessLogic<T> {

    void doing(T t) throws Exception;
}
