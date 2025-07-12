package app.entity;

import java.time.LocalDate; // または LocalDateTime

/**
 * チェックイン/チェックアウト日時を表すエンティティ
 */
public class CheckinDate {
    private LocalDate datetime; // クラス図の「日時」に相当

    public CheckinDate(LocalDate datetime) {
        this.datetime = datetime;
    }

    public LocalDate getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDate datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "CheckinDate{" +
               "datetime=" + datetime +
               '}';
    }
}
