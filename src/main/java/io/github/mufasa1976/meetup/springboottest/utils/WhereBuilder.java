package io.github.mufasa1976.meetup.springboottest.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

public class WhereBuilder {
  private static final String DOUBLE_QUOTE = "\"";

  private final BooleanBuilder predicate = new BooleanBuilder();

  public WhereBuilder googleLike(String value, StringPath... paths) {
    Optional.ofNullable(value)
            .filter(stringValue -> !StringUtils.isEmpty(stringValue))
            .map(stringValue -> Stream.of(paths)
                                      .map(path -> {
                                        if (stringValue.startsWith(DOUBLE_QUOTE) && stringValue.endsWith(DOUBLE_QUOTE)) {
                                          return path.eq(stringValue);
                                        }
                                        return path.like(String.format("%%%s%%", stringValue.replace('*', '%')
                                                                                            .replace('?', '_')));
                                      }).toArray(Predicate[]::new))
            .ifPresent(predicate::andAnyOf);
    return this;
  }

  public WhereBuilder googleLikeIgnoringCase(String value, StringPath... paths) {
    Optional.ofNullable(value)
            .filter(val -> !StringUtils.isEmpty(val))
            .map(stringValue -> Stream.of(paths)
                                      .map(path -> {
                                        if (stringValue.startsWith(DOUBLE_QUOTE) && stringValue.endsWith(DOUBLE_QUOTE)) {
                                          return path.equalsIgnoreCase(stringValue);
                                        }
                                        return path.likeIgnoreCase(String.format("%%%s%%", stringValue.replace('*', '%')
                                                                                                      .replace('?', '_')));
                                      }).toArray(Predicate[]::new))
            .ifPresent(predicate::andAnyOf);
    return this;
  }

  public WhereBuilder lowerThanOrEqualToStartOfDay(LocalDate value, DateTimePath<OffsetDateTime> path, ZoneId zoneId) {
    Optional.ofNullable(value)
            .map(localDate -> localDate.atTime(LocalTime.MIN))
            .map(localDateTime -> OffsetDateTime.of(localDateTime, (zoneId != null ? zoneId : ZoneId.systemDefault()).getRules().getOffset(localDateTime)))
            .map(path::loe)
            .ifPresent(predicate::and);
    return this;
  }

  public WhereBuilder lowerThanOrEqualToStartOfDay(LocalDate value, DateTimePath<OffsetDateTime> path) {
    return lowerThanOrEqualToStartOfDay(value, path, ZoneId.systemDefault());
  }

  public WhereBuilder greaterThanOrEqualToEndOfDay(LocalDate value, DateTimePath<OffsetDateTime> path, ZoneId zoneId) {
    Optional.ofNullable(value)
            .map(localDate -> localDate.atTime(LocalTime.MAX))
            .map(localDateTime -> OffsetDateTime.of(localDateTime, (zoneId != null ? zoneId : ZoneId.systemDefault()).getRules().getOffset(localDateTime)))
            .map(path::goe)
            .ifPresent(predicate::and);
    return this;
  }

  public WhereBuilder greaterThanOrEqualToEndOfDay(LocalDate value, DateTimePath<OffsetDateTime> path) {
    return greaterThanOrEqualToEndOfDay(value, path, ZoneId.systemDefault());
  }

  public Predicate where() {
    return predicate;
  }
}
