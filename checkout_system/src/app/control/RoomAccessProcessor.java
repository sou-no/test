package app.control;

import app.entity.RoomAccess;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.temporal.ChronoUnit; // このimportは前回追加済み

/**
 * 入退室処理を制御するクラス
 * クラス図の「入退室処理」に相当
 */
public class RoomAccessProcessor {

    private Map<Integer, RoomAccess> roomAccessRecordsByRoomNumber;

    public RoomAccessProcessor() {
        this.roomAccessRecordsByRoomNumber = new HashMap<>();
        // テストデータ (部屋番号と紐付けて生成)
        // RoomAccess(checkinId, roomNumber, checkinDate)
        roomAccessRecordsByRoomNumber.put(101, new RoomAccess(1001, 101, LocalDate.of(2025, 7, 10))); // 部屋番号101
        roomAccessRecordsByRoomNumber.put(205, new RoomAccess(1002, 205, LocalDate.of(2025, 7, 11))); // 部屋番号205
        
        roomAccessRecordsByRoomNumber.put(302, new RoomAccess(1003, 302, LocalDate.of(2025, 7, 12))); // 部屋番号302
    }

    /**
     * チェックアウト処理を実行します。
     * クラス図の「チェックアウト()」に相当
     * @param roomNumber チェックアウト対象の部屋番号
     * @return 処理が成功した場合はtrue、それ以外はfalse
     */
    public boolean processCheckout(int roomNumber) {
        System.out.println("チェックアウト処理を開始します。部屋番号: " + roomNumber);

        RoomAccess roomAccess = roomAccessRecordsByRoomNumber.get(roomNumber);

        if (roomAccess == null) {
            System.err.println("エラー: 指定された部屋番号の記録が見つかりません。部屋番号: " + roomNumber);
            return false;
        }

        if (roomAccess.isCheckedOut()) {
            System.out.println("情報: この部屋番号 (" + roomNumber + ") はすでにチェックアウト済みです。");
            return false;
        }

        try {
            double totalFee = roomAccess.calculateTotalFee();
            System.out.printf("確定した料金は %.2f 円です。\n", totalFee);
            return true;
        } catch (Exception e) {
            System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
            return false;
        }
    }

    /**
     * 支払い完了後にチェックアウトを確定するメソッド
     * @param roomNumber 部屋番号
     * @return 成功した場合true
     */
    public boolean confirmCheckoutCompletion(int roomNumber) {
        RoomAccess roomAccess = roomAccessRecordsByRoomNumber.get(roomNumber);

        if (roomAccess == null || roomAccess.isCheckedOut()) {
            System.err.println("エラー: チェックアウト確定処理の対象が見つからないか、すでにチェックアウト済みです。部屋番号: " + roomNumber);
            return false;
        }

        try {
            roomAccess.checkout(); // RoomAccessエンティティのcheckoutメソッドを呼び出す
            // データベースなどへの永続化処理がここに入る
            return true;
        } catch (IllegalStateException e) {
            System.err.println("チェックアウトエラー: " + e.getMessage());
            return false;
        }
    }


    /**
     * 利用状況を表示します。
     * クラス図の「現在の利用状況を表示する()」に相当
     */
    public void displayCurrentUsage() {
        System.out.println("\n--- 現在の入退室状況 ---");
        if (roomAccessRecordsByRoomNumber.isEmpty()) {
            System.out.println("現在、入室中の記録はありません。");
            return;
        }

        roomAccessRecordsByRoomNumber.forEach((roomNum, record) -> {
            String status = record.isCheckedOut() ? "チェックアウト済み" : "入室中";
            long stayDays;
            String stayDaysLabel;

            if (record.isCheckedOut()) {
                // チェックアウト済みの場合は確定した滞在日数を表示
                stayDays = ChronoUnit.DAYS.between(record.getCheckinDate(), record.getCheckoutDate());
                stayDaysLabel = stayDays + "日"; // (仮)を外す
            } else {
                // チェックアウト前の場合は仮の滞在日数を表示
                stayDays = ChronoUnit.DAYS.between(record.getCheckinDate(), LocalDate.now());
                stayDaysLabel = stayDays + "日(仮)"; // (仮)を残す
            }

            // 宿泊日数が0日の場合の調整（当日チェックイン・アウトを1日とする）
            if (stayDays == 0 && record.getCheckinDate().equals(record.getCheckoutDate())) {
                stayDaysLabel = "1日"; // 同日チェックイン・アウト
            } else if (stayDays == 0 && !record.isCheckedOut()) {
                 stayDaysLabel = "1日(仮)"; // 当日チェックインでまだチェックアウトしていない
            }


            System.out.println("部屋番号: " + roomNum +
                               ", 状態: " + status +
                               ", 入室日: " + record.getCheckinDate() +
                               ", 滞在日数: " + stayDaysLabel);

            if (record.isCheckedOut()) {
                System.out.println("  チェックアウト日: " + record.getCheckoutDate());
            }
        });
        System.out.println("------------------------\n");
    }

    // その他のメソッド (チェックイン、予約番号の取得など) は省略
}