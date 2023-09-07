<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
	</head>
	<body>
		<c:forEach var="cnt" begin="0" end="9">
			<p>야호</p>
		</c:forEach>
		아이디: <input type=text name=id id=id><p>
		암호: <input type=password name=pass id=pass><p>
		이름: <input type=text name=name id=name><p>
		<input type="button" value="확인" id="chk" style="color:red;" 
					onclick="location.href='jsRunTestResult.do?id=${id}&pass=${pass}&name=${name}'">
	</body>
</html>