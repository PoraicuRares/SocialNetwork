package com.lab.ui;

import com.lab.util.paging.Page;

import java.util.Scanner;
import java.util.function.BiFunction;

/**
 * Shows a list one page at a time in the console and lets the user move to the next or previous page.
 * The page is loaded again at every step, so the number of pages follows additions and removals.
 */
public class PageIterator {
    private final Scanner scanner;
    private final int pageSize;

    public PageIterator(Scanner scanner, int pageSize) {
        this.scanner = scanner;
        this.pageSize = pageSize;
    }

    /**
     * Shows the pages of a list until the user chooses to exit.
     *
     * @param loadPage receives the page number and the page size and returns that page
     * @param <T>      the type of the elements in the list
     */
    public <T> void browse(BiFunction<Integer, Integer, Page<T>> loadPage) {
        int pageNumber = 0;
        while (true) {
            Page<T> page = loadPage.apply(pageNumber, pageSize);
            if (page.totalNumberOfElements() == 0) {
                System.out.println("There are no entities");
                return;
            }

            int totalPages = (page.totalNumberOfElements() - 1) / pageSize + 1;

            System.out.println("Page " + (pageNumber + 1) + "/" + totalPages + ":");
            page.elementsOnPage().forEach(u -> System.out.println(" " + u));
            System.out.println("[n]ext , [p]revious , [e]xit : ");

            switch (scanner.nextLine().trim().toLowerCase()) {
                case "n" -> {
                    if (pageNumber == totalPages - 1) {
                        System.out.println("This is the last page");
                    } else {
                        pageNumber++;
                    }
                }
                case "p" -> {
                    if (pageNumber == 0) {
                        System.out.println("This is the first page");
                    } else {
                        pageNumber--;
                    }
                }
                case "e" -> {
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }
}
