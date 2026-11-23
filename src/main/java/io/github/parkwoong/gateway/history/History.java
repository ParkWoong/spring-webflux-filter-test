package io.github.parkwoong.gateway.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_history")
@IdClass(value = HistoryIdClass.class)
@Builder
public class History {

    @Builder.Default
    private String partitionTime =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

    @Id private String globId;

    private String uri;

    @Builder.Default @Id private LocalDateTime requestTime = LocalDateTime.now();

    private String requestType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String requestHeaders;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Builder.Default private LocalDateTime responseTime = LocalDateTime.now();

    private String responseStatus;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String responseBody;
}
