package kpn.server.api.authentication

import javax.crypto.KeyGenerator
import org.apache.commons.codec.binary.Base64.encodeBase64String

object CryptoGenerateKey {
  def main(args: Array[String]): Unit = {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(128)
    println(encodeBase64String(keyGen.generateKey.getEncoded))
  }
}
