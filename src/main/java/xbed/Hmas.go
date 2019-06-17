package main

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"fmt"
	"time"
)

func ComputeHmac256(timestamp string, secret string) string {
	key := []byte(secret)
	h := hmac.New(sha256.New, key)
	h.Write([]byte(timestamp))
	tempma := base64.StdEncoding.EncodeToString(h.Sum(nil))
	return tempma[:7] + timestamp
}

func NewCertPwX(secret string, privateKey string) bool {
	key := []byte(secret)
	mac := hmac.New(sha256.New, key)
	fmt.Println(privateKey[7:])
	mac.Write([]byte(privateKey[7:]))
	//tempma:= fmt.Sprintf("%x\n",mac.Sum(nil))

	tempma := base64.StdEncoding.EncodeToString(mac.Sum(nil))
	fmt.Println(tempma)

	return tempma[:7] == privateKey[:7]

}

func main() {
	timestamp := fmt.Sprint(time.Now().UnixNano() / 1000000)
	fmt.Println(timestamp)

	v := ComputeHmac256("1526101049605", "nkbn3zcry")
	fmt.Println(v)

	r := NewCertPwX("nkbn3zcry", v)
	fmt.Println(r)
}
