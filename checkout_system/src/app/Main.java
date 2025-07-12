package app;

import app.control.RoomAccessProcessor;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RoomAccessProcessor processor = new RoomAccessProcessor();
        Scanner scanner = new Scanner(System.in);

        try {
            // ステップ1: 現在の入退室状況を表示
            processor.displayCurrentUsage();

            // ステップ2: 自分で入力した部屋番号のチェックアウト処理を行う
            System.out.print("チェックアウトする部屋番号を入力してください: ");
            int roomNumberToCheckout = scanner.nextInt(); // ユーザーからの整数入力を受け取る
            scanner.nextLine(); // 改行文字を消費

            System.out.println("\n--- チェックアウト処理実行中 ---");
            boolean processInitiated = processor.processCheckout(roomNumberToCheckout); // 料金表示までを行う

            if (processInitiated) {
                // ステップ3a: 料金の支払いは完了したか？と聞きユーザがyes/noで回答
                System.out.print("料金の支払いは完了しましたか？ (yes/no): ");
                String paymentConfirmed = scanner.nextLine().trim().toLowerCase();

                if ("yes".equals(paymentConfirmed) || "y".equals(paymentConfirmed)) {
                    // ステップ3b: 支払いが完了した場合、チェックアウトを確定
                    boolean checkoutCompleted = processor.confirmCheckoutCompletion(roomNumberToCheckout);

                    // ステップ4: 処理の完了を通達
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

            // ステップ5: 更新された現在の入退室状況を表示
            processor.displayCurrentUsage();

        } catch (java.util.InputMismatchException e) {
            System.err.println("入力エラー: 無効な形式の番号が入力されました。整数を入力してください。");
        } catch (Exception e) {
            System.err.println("予期せぬエラーが発生しました: " + e.getMessage());
        } finally {
            scanner.close(); // Scannerを閉じ、リソースを解放する
        }
    }
}