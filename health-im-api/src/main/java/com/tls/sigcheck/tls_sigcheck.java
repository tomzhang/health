package com.tls.sigcheck;

public class tls_sigcheck
{
  protected int expireTime;
  protected int initTime;
  protected String strErrMsg;
  protected String strJsonWithSig;

  public void loadJniLib(String paramString)
  {
    System.load(paramString);
  }

  @Deprecated
  public native int tls_gen_signature_ex(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6);

  public native int tls_gen_signature_ex2(String paramString1, String paramString2, String paramString3);

  public native int tls_gen_signature_ex2_with_expire(String paramString1, String paramString2, String paramString3, String paramString4);

  @Deprecated
  public native int tls_check_signature_ex(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6);

  public native int tls_check_signature_ex2(String paramString1, String paramString2, String paramString3, String paramString4);

  public int getExpireTime()
  {
    return this.expireTime;
  }

  public int getInitTime()
  {
    return this.initTime;
  }

  public String getErrMsg()
  {
    return this.strErrMsg;
  }

  public String getSig() {
    return this.strJsonWithSig;
  }
}
