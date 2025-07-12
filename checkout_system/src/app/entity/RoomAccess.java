package app.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // 滞在日数計算用

/**
 * 入退室情報を管理するエンティティ
 * クラス図の「入退室」に相当
 */
public class RoomAccess {
    private int checkinId;
    private int checkoutId;
    private int roomNumber; // ★追加：部屋番号
    private LocalDate checkinDate;
    private LocalDate checkoutDate;

    // コンストラクタ
    public RoomAccess(int checkinId, int roomNumber, LocalDate checkinDate) { // ★変更：roomNumberを追加
        this.checkinId = checkinId;
        this.roomNumber = roomNumber; // ★追加
        this.checkinDate = checkinDate;
        this.checkoutId = 0;
        this.checkoutDate = null;
    }

    // --- ゲッター ---
    public int getCheckinId() {
        return checkinId;
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public int getRoomNumber() { // ★追加
        return roomNumber;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    // --- セッター ---
    public void setCheckout(int checkoutId, LocalDate checkoutDate) {
        if (this.checkoutDate != null) {
            throw new IllegalStateException("すでにチェックアウト済みです。");
        }
        this.checkoutId = checkoutId;
        this.checkoutDate = checkoutDate;
        System.out.println("チェックアウト日: " + this.checkoutDate); // ここでは「完了」の通達はせず、Mainで一括して行う
    }

    public void checkout() {
        setCheckout(generateRandomCheckoutId(), LocalDate.now());
    }

    private int generateRandomCheckoutId() {
        return (int) (Math.random() * 100000) + 1;
    }

    public boolean isCheckedOut() {
        return this.checkoutDate != null;
    }

    /**
     * ★追加：仮の料金計算メソッド
     * 宿泊日数に基づいて料金を計算します。
     * 実際には、部屋タイプ、プラン、追加サービスなどを考慮します。
     */
    public double calculateTotalFee() {
        if (checkinDate == null) {
            return 0.0;
        }

        LocalDate endDate = (checkoutDate != null) ? checkoutDate : LocalDate.now();
        long stayDays = ChronoUnit.DAYS.between(checkinDate, endDate);
        if (stayDays == 0 && checkoutDate == null) { // チェックアウト前で当日チェックインの場合
             stayDays = 1; // 最低1日として計算
        } else if (stayDays == 0 && checkoutDate != null) { // 同日チェックイン・チェックアウトの場合
             stayDays = 1;
        }

        // 仮の1泊あたりの料金 (例: 10000円)
        double pricePerNight = 10000.0;
        return stayDays * pricePerNight;
    }


    @Override
    public String toString() {
        return "RoomAccess{" +
               "checkinId=" + checkinId +
               ", roomNumber=" + roomNumber + // ★追加
               ", checkoutId=" + checkoutId +
               ", checkinDate=" + checkinDate +
               ", checkoutDate=" + checkoutDate +
               '}';
    }
}