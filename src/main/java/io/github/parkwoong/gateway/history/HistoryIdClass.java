package io.github.parkwoong.gateway.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryIdClass implements Serializable {
    private String globId;
    private LocalDateTime requestTime;
}
