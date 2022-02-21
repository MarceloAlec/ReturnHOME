<?php
header("Content-Type: application/json");
//CREDENCIALES DE ACCESO A LA BASE DE DATOS
$hostname = "localhost";
$username = "root";
$password = "1998*";
$database_name = "dbreturnhome";


if(isset($_GET["opcion"]) && isset($_GET["id"])){

    $opcion = $_GET["opcion"];
    $id = $_GET["id"];

    //CONEXION A LA BASE DE DATOS
    $conexion=new PDO("mysql:host=".$hostname.";dbname=".$database_name."",
                                    $username,
                                    $password,
                                    array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
        
    switch($opcion){
        case 1:
        
            $consulta = "SELECT * FROM tblmascota WHERE idCliente = ?";
            $stmt = $conexion->prepare($consulta);
            $stmt->bindValue(1,$id);
            $stmt->execute();
            $pets_num= $stmt->rowCount();

            if($pets_num > 0){

                $array_mascotas = array();
            
                while ($fila = $stmt->fetch(PDO::FETCH_ASSOC)){
                    extract($fila);
                    $e = array("idMascota" => $idMascota,
                                "nombre" => $nombre,
                                "raza" => $raza,
                                "genero" => $genero,
                                "descripcion" => $descripcion,
                                "idCliente" => $idCliente);
            
                    array_push($array_mascotas, $e);
                }
                $stmt=null;

                http_response_code(200);
                echo json_encode(array("mascotas" => $array_mascotas)); 
            }
            else{
                http_response_code(404);
            }
            break;

        case 2:
            $consulta = "SELECT *, tblcliente.nombre AS nombreCliente, tblmascota.nombre AS nombreMascota, tblmascota.genero AS generoMascota
                        FROM tblcliente
                        INNER JOIN tblmascota on tblcliente.idCliente = tblmascota.idCliente 
                        WHERE idMascota = ?";

            $stmt = $conexion->prepare($consulta);
            $stmt->bindValue(1,$id);
            $stmt->execute();
            $num_mascotas= $stmt->rowCount();

            if($num_mascotas == 1){

                $resultado = $stmt->fetch(PDO::FETCH_ASSOC);
                $stmt=null;

                $mascota = array("idMascota" => $resultado["idMascota"],
                                 "nombre" => $resultado["nombreMascota"],
                                 "raza" => $resultado["raza"],
                                 "genero" => $resultado["generoMascota"],
                                 "descripcion" => $resultado["descripcion"],
                                 "desaparecida" => filter_var($resultado["desaparecida"], FILTER_VALIDATE_BOOLEAN),
                                 "idCliente" => $resultado["idCliente"]);

                $cliente = array("idCliente" => $resultado["idCliente"],
                                 "nombre" => $resultado["nombreCliente"],
                                 "email" => $resultado["email"],
                                 "numeroCelular" => $resultado["numeroCelular"]);

                http_response_code(200);
                echo json_encode(array("mascota" => $mascota, "cliente" => $cliente));
            
            }
            else{
                http_response_code(404);
            }

            break;
        case 3:
            
            $consulta = "SELECT * FROM tblmascota WHERE desaparecida = true";
            $stmt = $conexion->prepare($consulta);
            $stmt->execute();
            $num_mascotaDesaparecida= $stmt->rowCount();

            if($num_mascotaDesaparecida > 0){

                $array_mascotas = array();
            
                while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    extract($row);
                    $e = array("idMascota" => $idMascota,
                               "nombre" => $nombre,
                               "raza" => $raza,
                               "genero" => $genero,
                               "descripcion" => $descripcion,
                               "idCliente" => $idCliente);
                
                    array_push($array_mascotas, $e);
                }
                $stmt=null;
                http_response_code(200);
                echo json_encode(array("mascotas" => $array_mascotas));
            }
            else{
                http_response_code(404);
            }
            break;
        default:
            http_response_code(404);
    }
}
else{
    http_response_code(404);
}

?>