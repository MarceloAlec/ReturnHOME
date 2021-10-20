<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

require_once '../connection.php' ;


// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

 //PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
 $parameters = file_get_contents('php://input');
 $data = json_decode($parameters, true);

$sqlQuery = "insert into tblclient (name, email, pass, gender, phoneNumber) values(:name, :email, :pass, :gender, :phoneNumber)";
//MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
//PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
$stmt = $pdo->prepare($sqlQuery);
$name = $data["name"];
$email = $data["email"];
$password = password_hash($data["password"],PASSWORD_DEFAULT);
$gender = $data["gender"];
$phoneNumber = $data["phoneNumber"];

//CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
$stmt->bindValue(':name',$name);
$stmt->bindValue(':email',$email);
$stmt->bindValue(':pass',$password);
$stmt->bindValue(':gender',$gender);
$stmt->bindValue(':phoneNumber',$phoneNumber);
//EJECUTA LA SENTENCIA SQL, ENVIA LOS DATOS A LA BASE
$stmt->execute();
/* $idPost = $pdo->lastInsertId();
if($idPost){
    $val = array("id" => $idPost);
    echo json_encode($val);
} */

/*  $versiones = array(
    "versiones_android" => array(
        array(
            "froyo" => "2.2"
        ),
        array(
            "gingerbread" => "2.3"
        )
    )
); */

$versiones = array("status"=>"201");
echo json_encode($versiones);
    
?>




