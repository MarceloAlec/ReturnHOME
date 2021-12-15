<?php

class Client{

    private $tblClient = "tblclient";

    public function __construct(){

    }

    public function authClient($connection, $email, $password){
        $query = "SELECT * FROM " . $this->tblClient . " WHERE email = ?";
        //MEDIANTE PREPARE() SE CREA UNA INSTANCIA DE LA CLASE PDO STATEMENT
        //PARA PASAR LOS VALORES O EJECUTAR SENTENCIAS
        $stmt = $connection->prepare($query);
        //CON BINDVALUE SE ENLAZA EL VALOR DE LA VARIABLE
        $stmt->bindValue(1,$email);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num>0){
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            // VERIFICA LA CONSTRASEÑA
            if (password_verify($password, $result["pass"])) {
                return $result;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public function createClient($connection, $name, $email, $password, $gender, $phoneNumber, $token){

        $query = "SELECT * FROM " . $this->tblClient . " WHERE email = ?";

        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$email);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num == 0){
            $query = "INSERT INTO ".$this->tblClient . "(name, email, pass, gender, phoneNumber, token) values(?, ?, ?, ?, ?, ?)";

            $stmt = $connection->prepare($query);
            $stmt->bindValue(1,$name);
            $stmt->bindValue(2,$email);
            $stmt->bindValue(3,$password);
            $stmt->bindValue(4,$gender);
            $stmt->bindValue(5,$phoneNumber);
            $stmt->bindValue(6,$token);

            if($stmt->execute()){
                $idClient=$connection->lastInsertId();
                return array("id"=>$idClient);
            }
        }
        return false;
    }

    public function readClient($connection, $idClient){
        $query = "SELECT * FROM " . $this->tblClient . " WHERE id = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$idClient);
        $stmt->execute();
        $pets_num= $stmt->rowCount();

        if($pets_num == 1){
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            return array("id" => $result["id"],
                        "name" => $result["name"],
                        "email" => $result["email"],
                        "gender" => $result["gender"],
                        "phoneNumber" => $result["phoneNumber"],
                        "token" => $result["token"]);
            
        }
        else{
           return false;
        }
    }

    public function updateProfile($connection, $id, $name, $email, $gender, $phoneNumber){
        $query = "UPDATE " . $this->tblClient . " SET name = ?,email = ?,gender = ?,phoneNumber = ? WHERE id = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$name);
        $stmt->bindValue(2,$email);
        $stmt->bindValue(3,$gender);
        $stmt->bindValue(4,$phoneNumber);
        $stmt->bindValue(5,$id);
        $stmt->execute();
        $rows_affected = $stmt->rowCount();
        if($rows_affected==1){
            return true;
        }
        else{
            return false;
        }
    }

    public function updateToken($connection, $id, $token){
        $query = "UPDATE " . $this->tblClient . " SET token = ? WHERE id = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$token);
        $stmt->bindValue(2,$id);
        
        $stmt->execute();
       
        if($stmt){
            return true;
        }
        else{
            return false;
        }
    }

    public function updatePassword($connection, $id, $newPassword, $currentPassword){
        
        $query = "SELECT pass FROM " . $this->tblClient . " WHERE id = ?";
       
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$id);
        $stmt->execute();

        $num = $stmt->rowCount();

        if($num==1){
            $result = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if (password_verify($currentPassword, $result["pass"])) {
                $query = "UPDATE " . $this->tblClient . " SET pass = ? WHERE id = ?";
                $stmt = $connection->prepare($query);
                $stmt->bindValue(1,password_hash($newPassword,PASSWORD_DEFAULT));
                $stmt->bindValue(2,$id);
                $stmt->execute();
                $rows_affected = $stmt->rowCount();

                if($rows_affected==1){
                    return true;
                }           
            } 
        }

        return false;
    }

    public function deleteClient($connection, $id){
        $query = "DELETE FROM " . $this->tblClient . " WHERE id = ?";
        $stmt = $connection->prepare($query);
        $stmt->bindValue(1,$id);
        $stmt->execute();
        $rows_affected = $stmt->rowCount();
        if($rows_affected==1){
            return true;
        }
        else{
            return false;
        }
    }


    





}



?>