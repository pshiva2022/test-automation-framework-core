package com.dummycompany.test.framework.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

public class AES {

  private static final Logger LOG = LogManager.getLogger(AES.class);
  private static SecretKeySpec secretKey;

  public static void setKey(String myKey) {
    MessageDigest sha = null;
    try {
      byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
      sha = MessageDigest.getInstance("SHA-1");
      key = sha.digest(key);
      key = Arrays.copyOf(key, 16);
      secretKey = new SecretKeySpec(key, "AES");
    } catch (NoSuchAlgorithmException e) {
      LOG.error(e.getStackTrace());
      throw new RuntimeException(e);
    }
  }

  public static String encrypt(String strToEncrypt, String secret) {
    Assert.assertTrue("Please set QA_TEST_DATA_ENCRYPTION_PASSWORD as environment variable with the correct secret!",
        StringUtils.isNotBlank(secret));
    try {
      setKey(secret);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      LOG.error("Error while encrypting: " + e);
      throw new RuntimeException(e);
    }
  }

  public static String decrypt(String strToDecrypt, String secret) {
    if (StringUtils.isBlank(secret)) {
      String msg = "You have called AES.decrypt(String strToDecrypt, String secret) method but didn't supply a valid secret. "
          + "A secret is unique value using which you may encrypt your passwords and add them into config files. "
          + "Take a look of src/test/java/domain/utils/EncryptDecryptUtil.java for more details. "
          + "If you are a new user and just trying out yantra framework by executing sample test case provided then please supply -DQA_TEST_DATA_ENCRYPTION_PASSWORD=Password@123 ";
      LOG.error(msg);
      throw new IllegalArgumentException(msg);
    }
    try {
      setKey(secret);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    } catch (Exception e) {
      LOG.error("Error while decrypting: " + e);
      throw new RuntimeException(" **** Error while decrypting *** ===>  ", e);
    }
  }
}
