<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");



require_once '../connection.php' ;


// CREA LA CONEXION A LA BASE DE DATOS
$pdo = new Connection();

 //PHP://INPUT PERMITE LEER DATOS DE UN CUERPO SOLICITADO
 $parameters = file_get_contents('php://input');
 $data = json_decode($parameters, true);

$sql = "insert into tblclient (name, email, pass) values(:name, :email, :pass)";
//MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
//PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
$stmt = $pdo->prepare($sql);

//CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
$stmt->bindValue(':name',$data["name"]);
$stmt->bindValue(':email',$data["email"]);
$stmt->bindValue(':pass',$data["pass"]);
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

$versiones = array("estado"=>"201");
echo json_encode($versiones);
    





