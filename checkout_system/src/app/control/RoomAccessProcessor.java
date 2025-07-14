package app.control;

import app.entity.RoomAccess;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.temporal.ChronoUnit;

public class RoomAccessProcessor {

    // 部屋番号 -> 入退室情報 のマッピング (チェックイン済み用)
    private Map<Integer, RoomAccess> roomAccessRecordsByRoomNumber;
    // 予約番号 -> 部屋番号 のマッピング (予約管理用)
    private Map<Integer, Integer> reservedRoomNumbersByReservationId; // ★追加: 予約管理用Map
    // 予約番号 -> 予約日 のマッピング (予約管理用 - より詳細な予約情報が必要な場合)
    // private Map<Integer, LocalDate> reservationDatesByReservationId; // 必要に応じて

    public RoomAccessProcessor() {
        this.roomAccessRecordsByRoomNumber = new HashMap<>();
        this.reservedRoomNumbersByReservationId = new HashMap<>(); // ★初期化

        // --- テストデータ ---
        // 既存の入室中データ
        roomAccessRecordsByRoomNumber.put(101, new RoomAccess(1001, 101, LocalDate.of(2025, 7, 10))); // 部屋番号101
        roomAccessRecordsByRoomNumber.put(205, new RoomAccess(1002, 205, LocalDate.of(2025, 7, 11))); // 部屋番号205
        roomAccessRecordsByRoomNumber.put(302, new RoomAccess(1003, 302, LocalDate.of(2025, 7, 12))); // 部屋番号302

        // ★追加: ダミーの予約データ
        // (予約番号, 部屋番号)
        reservedRoomNumbersByReservationId.put(5001, 102); // 予約番号5001で部屋102を予約
        reservedRoomNumbersByReservationId.put(5002, 201); // 予約番号5002で部屋201を予約
        reservedRoomNumbersByReservationId.put(5003, 301); // 予約番号5003で部屋301を予約
    }

    /**
     * ★追加: 予約キャンセル処理
     * @param reservationId キャンセルする予約番号
     * @return キャンセルが成功した場合はtrue、それ以外はfalse
     */
    public boolean cancelReservation(int reservationId) {
        System.out.println("予約キャンセル処理を開始します。予約番号: " + reservationId);

        if (!reservedRoomNumbersByReservationId.containsKey(reservationId)) {
            System.err.println("エラー: 指定された予約番号が見つかりません。予約番号: " + reservationId);
            return false;
        }

        // 予約を削除する
        Integer roomNumber = reservedRoomNumbersByReservationId.remove(reservationId);
        if (roomNumber != null) {
            System.out.println("予約番号 " + reservationId + " の部屋 " + roomNumber + " の予約をキャンセルしました。");
            // 実際には、ここで部屋の空き状況を更新するなどの処理が入る
            return true;
        } else {
            // ここには通常到達しないはず
            System.err.println("エラー: 予約の削除中に問題が発生しました。予約番号: " + reservationId);
            return false;
        }
    }

    /**
     * ★追加: チェックイン処理 (簡易版)
     * 予約番号を使ってチェックインするシナリオを想定。
     * 実際には、予約情報から顧客情報や部屋タイプなどを取得し、RoomAccessを詳細に構築する。
     * @param reservationId チェックインする予約番号
     * @return チェックインが成功した場合はtrue、それ以外はfalse
     */
    public boolean processCheckin(int reservationId) {
        System.out.println("チェックイン処理を開始します。予約番号: " + reservationId);

        // 予約が存在するか確認
        Integer roomNumber = reservedRoomNumbersByReservationId.get(reservationId);
        if (roomNumber == null) {
            System.err.println("エラー: 指定された予約番号が見つかりません。予約番号: " + reservationId);
            return false;
        }

        // その部屋がすでにチェックイン済みでないか確認
        if (roomAccessRecordsByRoomNumber.containsKey(roomNumber) && !roomAccessRecordsByRoomNumber.get(roomNumber).isCheckedOut()) {
            System.err.println("エラー: 部屋番号 " + roomNumber + " はすでにチェックイン済みです。");
            return false;
        }

        // 仮のチェックインIDを生成し、新しい入退室記録を作成
        int newCheckinId = (int) (Math.random() * 1000000) + 1;
        RoomAccess newRoomAccess = new RoomAccess(newCheckinId, roomNumber, LocalDate.now());
        roomAccessRecordsByRoomNumber.put(roomNumber, newRoomAccess);

        // チェックインが完了したら、予約リストから削除する (予約済みから入室中へ移行)
        reservedRoomNumbersByReservationId.remove(reservationId);

        System.out.println("予約番号 " + reservationId + " のお客様が部屋番号 " + roomNumber + " にチェックインしました。");
        return true;
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
            return true; // 料金表示と支払い確認の準備ができたことを示す
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
        System.out.println("\n--- 現在の入室状況 ---");
        if (roomAccessRecordsByRoomNumber.isEmpty()) {
            System.out.println("現在、入室中の記録はありません。");
        } else {
            roomAccessRecordsByRoomNumber.forEach((roomNum, record) -> {
                String status = record.isCheckedOut() ? "チェックアウト済み" : "入室中";
                long stayDays;
                String stayDaysLabel;

                if (record.isCheckedOut()) {
                    stayDays = ChronoUnit.DAYS.between(record.getCheckinDate(), record.getCheckoutDate());
                    stayDaysLabel = stayDays + "日";
                } else {
                    stayDays = ChronoUnit.DAYS.between(record.getCheckinDate(), LocalDate.now());
                    stayDaysLabel = stayDays + "日(仮)";
                }

                if (stayDays == 0 && record.getCheckinDate().equals(record.getCheckoutDate())) {
                    stayDaysLabel = "1日";
                } else if (stayDays == 0 && !record.isCheckedOut()) {
                     stayDaysLabel = "1日(仮)";
                }

                System.out.println("部屋番号: " + roomNum +
                                   ", 状態: " + status +
                                   ", 入室日: " + record.getCheckinDate() +
                                   ", 滞在日数: " + stayDaysLabel);

                if (record.isCheckedOut()) {
                    System.out.println("  チェックアウト日: " + record.getCheckoutDate());
                }
            });
        }
        System.out.println("-----------------------\n");


        System.out.println("--- 現在の予約状況 ---"); // ★追加: 予約状況の表示
        if (reservedRoomNumbersByReservationId.isEmpty()) {
            System.out.println("現在、未処理の予約はありません。");
        } else {
            reservedRoomNumbersByReservationId.forEach((resId, roomNum) -> {
                System.out.println("予約番号: " + resId + ", 部屋番号: " + roomNum + " (予約済み)");
            });
        }
        System.out.println("-----------------------\n");
    }

    // その他のメソッド (予約番号の取得など) は省略
}