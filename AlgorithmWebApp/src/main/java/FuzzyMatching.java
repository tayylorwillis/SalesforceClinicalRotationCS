package AlgorithmWebApp.src.main.java;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Fuzzy")
public class FuzzyMatching extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public FuzzyMatching() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("fuzzy_landing.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String text1 = request.getParameter("text1");
        String text2 = request.getParameter("text2");

        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            request.setAttribute("Error", "Please enter valid strings in both input fields.");
        } else {
            int distance = levenshteinDistance(text1, text2);
            double similarity = similarities(text1, text2);

            request.setAttribute("distance", distance);
            request.setAttribute("similarity", similarity);
            request.setAttribute("text1", text1);
            request.setAttribute("text2", text2);       
            }

        request.getRequestDispatcher("fuzzy_results.jsp").forward(request, response);
    }

    public static int levenshteinDistance(String text, String pattern) {
        int[][] doubleArray = new int[text.length() + 1][pattern.length() + 1];

        for (int i = 0; i <= text.length(); i++) {
            for (int j = 0; j <= pattern.length(); j++) {
                if (i == 0) {
                    doubleArray[i][j] = j;
                } else if (j == 0) {
                    doubleArray[i][j] = i;
                } else {
                    doubleArray[i][j] = Math.min(Math.min(
                            doubleArray[i - 1][j - 1] + (text.charAt(i - 1) == pattern.charAt(j - 1) ? 0 : 1),
                            doubleArray[i - 1][j] + 1),
                            doubleArray[i][j - 1] + 1);
                }
            }
        }
        return doubleArray[text.length()][pattern.length()];
    }

    public static double similarities(String text, String pattern) {
        int maximum = Math.max(text.length(), pattern.length());
        if (maximum == 0) {
            return 1.0;
        }
        return 1.0 - (double) levenshteinDistance(text, pattern) / maximum;
    }
}

