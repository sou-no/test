package app;

import app.control.RoomAccessProcessor;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RoomAccessProcessor processor = new RoomAccessProcessor();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("--- ホテル管理システム メニュー ---");
            System.out.println("現在の状況を表示します。");
            processor.displayCurrentUsage(); // 常に現在の状況を表示

            System.out.println("以下のメニューから選択してください:");
            System.out.println("1. チェックイン");
            System.out.println("2. チェックアウト");
            System.out.println("3. 予約のキャンセル");
            System.out.println("4. 終了");
            System.out.print("選択 (1-4): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 改行文字を消費

                switch (choice) {
                    case 1: // チェックイン
                        System.out.print("チェックインする予約番号を入力してください: ");
                        int reservationIdForCheckin = scanner.nextInt();
                        scanner.nextLine(); // 改行文字を消費
                        boolean checkinSuccess = processor.processCheckin(reservationIdForCheckin);
                        if (checkinSuccess) {
                            System.out.println("✅ チェックイン処理が正常に完了しました。");
                        } else {
                            System.out.println("❌ チェックイン処理に失敗しました。上記のエラーメッセージを確認してください。");
                        }
                        break;

                    case 2: // チェックアウト
                        System.out.print("チェックアウトする部屋番号を入力してください: ");
                        int roomNumberToCheckout = scanner.nextInt();
                        scanner.nextLine(); // 改行文字を消費

                        boolean processInitiated = processor.processCheckout(roomNumberToCheckout);
                        if (processInitiated) {
                            System.out.print("料金の支払いは完了しましたか？ (yes/no): ");
                            String paymentConfirmed = scanner.nextLine().trim().toLowerCase();

                            if ("yes".equals(paymentConfirmed) || "y".equals(paymentConfirmed)) {
                                boolean checkoutCompleted = processor.confirmCheckoutCompletion(roomNumberToCheckout);
                                if (checkoutCompleted) {
                                    System.out.println("✅ チェックアウト処理が正常に完了しました。");
                                } else {
                                    System.out.println("❌ チェックアウト処理に失敗しました。上記のエラーメッセージを確認してください。");
                                }
                            } else {
                                System.out.println("⚠ 支払いが完了していないため、チェックアウト処理は中断されました。");
                            }
                        } else {
                            System.out.println("❌ チェックアウト処理を開始できませんでした。上記のエラーメッセージを確認してください。");
                        }
                        break;

                    case 3: // 予約のキャンセル
                        System.out.print("キャンセルする予約番号を入力してください: ");
                        int reservationIdToCancel = scanner.nextInt();
                        scanner.nextLine(); // 改行文字を消費
                        boolean cancelSuccess = processor.cancelReservation(reservationIdToCancel);
                        if (cancelSuccess) {
                            System.out.println("✅ 予約キャンセル処理が正常に完了しました。");
                        } else {
                            System.out.println("❌ 予約キャンセル処理に失敗しました。上記のエラーメッセージを確認してください。");
                        }
                        break;

                    case 4: // 終了
                        System.out.println("システムを終了します。");
                        running = false;
                        break;

                    default:
                        System.out.println("無効な選択です。1から4の番号を入力してください。");
                        break;
                }
                System.out.println("\n-----------------------------------\n"); // 区切り線

            } catch (InputMismatchException e) {
                System.err.println("入力エラー: 無効な形式の入力です。数字を入力してください。");
                scanner.nextLine(); // 無効な入力をクリア
            } catch (Exception e) {
                System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
            }
        }
        scanner.close();
    }
}