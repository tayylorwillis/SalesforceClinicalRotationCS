<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fuzzy Matching Results</title>
</head>
<body>
    <h2>Fuzzy Matching Results</h2>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
        <p style="color: red;"><%= error %></p>
    <% } else { %>
            <p>Strings Tested:</p>
        <ul>
            <li>First String: <%= request.getAttribute("text1") %></li>
            <li>Second String: <%= request.getAttribute("text2") %></li>
        </ul>
        <p>Levenshtein Distance: <%= request.getAttribute("distance") %></p>
        <p>Similarity Score: <%= request.getAttribute("similarity") %></p>
    <% } %>

    <br>
    <a href="fuzzy_landing.html">Compare again</a>
</body>
</html>
