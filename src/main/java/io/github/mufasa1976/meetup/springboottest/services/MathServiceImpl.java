package io.github.mufasa1976.meetup.springboottest.services;

import org.springframework.stereotype.Service;

@Service
public class MathServiceImpl implements MathService {
  @Override
  public int divide(int dividend, int divisor) {
    return dividend / divisor;
  }
}
