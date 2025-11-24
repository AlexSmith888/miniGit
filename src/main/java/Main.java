import own.nio.request.CommandsDispatcher;
import own.nio.utils.InputProcessing;
import own.nio.validation.InputValidation;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    static void main() {
        GitInitializer.launch();
        Scanner scanner = new Scanner(System.in);
        String text;

        while (true) {
            GitInitializer.help();
            IO.println(" > ");
            text = scanner.nextLine().trim().toLowerCase();

            if (text.equals("exit")) {
                break;
            }

            try {

                new InputValidation().isValid(
                        InputProcessing.returnInitInput(text));
                new CommandsDispatcher().process(
                        InputProcessing.returnInitInput(text));

            } catch (IllegalArgumentException e) {
                IO.println("Illegal input parameters");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                IO.println("check whether directories / files exist");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
        GitInitializer.finish();
    }
}
