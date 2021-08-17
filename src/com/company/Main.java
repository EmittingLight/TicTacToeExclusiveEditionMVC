package com.company;

public class Main {
    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller();
        controller.setView(view);
        view.setController(controller);
        controller.start();

    }
}
