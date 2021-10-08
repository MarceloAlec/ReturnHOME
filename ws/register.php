<?php

include 'connection.php' ;
 
 $con = mysqli_connect($hostname,$hostuser,$hostpass,$database);
 
 $name = $_POST['name'];
 $email = $_POST['email'];
 $pass = $_POST['pass'];

 $sqlQuery = "insert into tblUser(name,email,pass) values ('$name','$email','$pass')";
 
 if(mysqli_query($con,$sqlQuery)){
 echo 'Datos enviados con éxito';
 }
 else{
 echo 'Inténtalo de nuevo';
 }
 mysqli_close($con);
?>