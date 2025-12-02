import cli.GitInitializer;
import cli.ResourcesLoader;
import domain.services.RequestsDispatcher;
import utils.CLiParser;
import app.validations.InputValidation;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String [] args) {
        GitInitializer.launch();
        ResourcesLoader.launch();
        Scanner scanner = new Scanner(System.in);
        String text;

        while (true) {
            GitInitializer.help();
            System.out.println(" > ");
            text = scanner.nextLine().trim().toLowerCase();

            if (text.equals("exit")) {
                break;
            }

            try {
                new InputValidation().isValid(
                        CLiParser.returnInitInput(text));
                new RequestsDispatcher().process(
                        CLiParser.returnInitInput(text));

            } catch (IllegalArgumentException e) {
                System.out.println("Illegal input parameters");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("Check whether directories / files exist");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
        ResourcesLoader.finish();
        GitInitializer.finish();
    }
}
