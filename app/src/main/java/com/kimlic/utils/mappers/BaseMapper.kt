package com.kimlic.utils.mappers

interface BaseMapper<in A, out B> {
  fun transform(input: A) : B
}