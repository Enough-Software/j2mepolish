/*
 * Created on 27-Nov-2005 at 14:08:51.
 *
 * HFloat - floating point arithmetics for mobile devices
 *
 * Copyright (c) 2009 Horst Jaeger / Medienkonzepte GbR, Cologne, Germany
 *
 * This file is part of J2ME Polish.
 *
 * HFloat is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * HFloat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You can receive a copy of the GNU General Public License
 * if you write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available indpendently of J2ME Polish, please mail
 * hfloat@medienkonzepte.de for details.
 * You can also use HFloat commercially when you have obtained a commercial
 * license of J2ME Polish. 
 *
 */
 
package de.enough.polish.math;

/*
 * This class implements floating point arithmetics and some maths. HFloats can be constructed from int like in new HFloat(3) or from String like in new HFloat("-3.14E-2") which means -0.0314 . If you don't want to use the E-Syntax, type new HFloat("-0.0314") instead.
 * 
 * The first argument of each operation is the HFloat object itself. E.g. if you want to know what 2.3 * 4.7 is, type
 * System.out.println((new HFloat("2.3")).mlt(new HFloat("4.7")).toString()); this yields "1.08100000E1" which is
 * just another way of writing 10.81 . HFloat will always use the scientific output format - there's no way of telling it
 * to use a different one.
 * 
 * In case of any invalid operation, the result will be NaN (Not A Number) . We did not want to waste Memory on additinal
 * classes so we did not define any HFloat-Exceptions.
 * 
 * Because a HFloat may be invalid, it can't be cast to int - it can be cast to Integer instead, using the toInteger()
 * function. The result will be null if the HFloat is NAN.
 * 
 * Two HFloats x and y can be compared using the cmp-Function. The result of x.cmp(y) will be -1 if x < y, 0 if x == y
 * and +1 if x > y . You can give a tolerance value eps as well using x.softCmp(y, eps, true) or x.softCmp(y, eps, false).
 * Then x and y will be considered equal if they differ less than or no more than eps respectively. The cmp and softCmp
 * functions both yield Integer instead of int so the result can be null in case of NaNs.
 * 
 * There are lots of examples about how to use HFloats in the HFloat.java source file as well.
 */public class HFloat extends Object{
  
//Internal

  protected class HFloatHTaylor extends Object implements HTaylor{
    
    public HFloatHTaylor(              ){init(        -1);}
    public HFloatHTaylor(int werBinIchP){init(werBinIchP);}
    
    protected void init(int werBinIchA){
      this.werBinIchP = werBinIchA;
      this.altN       = 0;
      this.altCoeff   = new HFloat(1); // haengt aus purem Zufall nicht von von werBinIchP ab
    }

    public HFloat coeff(int n){
      switch(this.werBinIchP){
        case ASIN_HTAYLOR:
          if(n < this.altN) init(this.werBinIchP);
          for(int m = this.altN + 1; m <= n; ++m){
            int m2 = 2 * m;
            this.altCoeff = this.altCoeff.mlt(m2 - 1).div(m2);      
          }
          this.altN = n;      
          return this.altCoeff.div(2 * n + 1);  
        case ATAN_HTAYLOR:
          if(n % 2 == 0) return (new HFloat( 1)).div(2 * n + 1);
          return (new HFloat(-1)).div(2 * n + 1);
        case COS_HTAYLOR:
          if(n < this.altN) init(this.werBinIchP);
          for(int m = this.altN + 1; m <= n; ++m){
            int m2 = 2 * m;
            this.altCoeff = this.altCoeff.div(m2 * (1 - m2));      
          }
          this.altN = n;      
          return this.altCoeff;  
        case EXP_HTAYLOR:
          if(n < this.altN) init(this.werBinIchP);
          for(int m = this.altN + 1; m <= n; ++m) this.altCoeff = this.altCoeff.div(m);      
          this.altN = n;      
          return this.altCoeff;  
        case LN_HTAYLOR:
          if(n % 2 == 0) return (new HFloat( 1)).div(n + 1);
          return (new HFloat(-1)).div(n + 1);
        case SIN_HTAYLOR:
         if(n < this.altN) init(this.werBinIchP);
         for(int m = this.altN + 1; m <= n; ++m){
           int m2 = 2 * m;
           this.altCoeff = this.altCoeff.div(-m2 * (1 + m2));      
         }
         this.altN = n;      
         return this.altCoeff;  
        default:
          return new HFloat();
      }
    }

    protected int    werBinIchP;
    protected int    altN;
    protected HFloat altCoeff;
    
  }  

//Constructors

  public HFloat(){HFloatInit(0, 0, false);}
  
  public HFloat(HFloat orig){HFloatInit(orig.mant, orig.expo, orig.valid);}

  public HFloat(int wertA){HFloatInit(wertA, 0, true);}

  public HFloat(int wertA, int expoA){HFloatInit(wertA, expoA, true);}

  public HFloat(String text){
    if(text.toLowerCase().equals("nan")){
      HFloatInit(0, 0, false);      
    }else{
      int[] hilf = format(text);
      HFloatInit(hilf[0], hilf[1], true);
    }
  }
  
//Cast

  public Integer toInteger(){
    if(!this.valid) return null;
    int     retval = this.mant;
    boolean rund   = false;
    for(int n = 0; n < this.expo; n++) retval *= 10;
    for(int n = 0; n > this.expo; n--){
      if(retval % 10 != 0) rund = true;
      retval /= 10;
    }
    if((this.mant < 0) && rund) retval--;
    return new Integer(retval);
  }

  public String toString(){
    if(!this.valid) return "NaN";
    int expoA;
    int cmpI = cmp().intValue();
    String wertS;
    if(cmpI == -1) return "-" + neg().toString();
    if(cmpI ==  0) expoA = 0;
    else{
       expoA = -1;
       for(int wertA = abs(this.mant); wertA > 0; wertA /= 10) expoA++;
    }
    wertS = (new Integer(this.mant)).toString();
    for(int n = wertS.length(); n < eInt; n++) wertS += "0";
    return wertS.substring(0, 1) + "." + wertS.substring(1, eInt) + "E" + (new Integer(expoA + this.expo)).toString();
  }
  
//  Member (public)

  public HFloat get(){return new HFloat(this);}

  public void set(HFloat orig){HFloatInit(orig.mant, orig.expo, orig.valid);}

  public HFloat add(HFloat arg){
    if(!this.valid || !arg.valid)      return NaN;
    if(    cmp().intValue() == 0) return arg.get();
    if(arg.cmp().intValue() == 0) return get();
    int wertH    = this.mant;
    int argWertH = arg.mant;
    int expoH    = this.expo;
    int argExpoH = arg.expo;
    while(expoH < argExpoH){
      expoH++;
      wertH /= 10;
    }
    while(expoH > argExpoH){
      argExpoH++;
      argWertH /= 10;
    }
    int expoR = expoH;
    int wertR = wertH + argWertH;
    while(wertR > iMax){
      expoR++;
      wertR /= 10;
    }
    return new HFloat(wertR, expoR);
  }
  
  public HFloat add(int    arg){return add(new HFloat(arg));}
  public HFloat add(String arg){return add(new HFloat(arg));}

  public HFloat neg(){
    if(!this.valid) return NaN;
    return new HFloat(-this.mant, this.expo);
  }
  
  public HFloat sbt(HFloat arg){return add(arg.neg());}

  public HFloat sbt(int    arg){return sbt(new HFloat(arg));}
  public HFloat sbt(String arg){return sbt(new HFloat(arg));}

  public HFloat mlt(HFloat arg){
    if(!this.valid || !arg.valid) return NaN;
    int[] hilf  = format(this.mant * (long) arg.mant);
    return new HFloat(hilf[0], this.expo + arg.expo + hilf[1]);
  }
  
  public HFloat mlt(int    arg){return mlt(new HFloat(arg));}
  public HFloat mlt(String arg){return mlt(new HFloat(arg));}

  public HFloat inv(){
    if(!this.valid || (cmp().intValue() == 0)) return NaN;
    int[] hilf  = format(lMax / this.mant);
    return new HFloat(hilf[0], -this.expo - eLong + hilf[1]);
  }
    
  public HFloat div(HFloat arg){return mlt(arg.inv());}
  
  public HFloat div(int    arg){return div(new HFloat(arg));}
  public HFloat div(String arg){return div(new HFloat(arg));}

  public HFloat abs(){
    if(!this.valid)   return NaN;
    if(this.mant < 0) return new HFloat(-this.mant, this.expo);
    else              return new HFloat( this.mant, this.expo);
  }
  
  public Integer cmp(){
    if(!this.valid)         return null;
    if     (this.mant == 0) return new Integer( 0);
    else if(this.mant  > 0) return new Integer( 1);
    else                    return new Integer(-1);
  }
  
  public Integer cmp(HFloat arg){return sbt(arg).cmp();}
  public Integer cmp(int    arg){return sbt(arg).cmp();}
  public Integer cmp(String arg){return sbt(arg).cmp();}
  
  public Integer softCmp(HFloat tol, boolean strict){
    if(!this.valid || !tol.valid) return null;
    int cmpVal = abs().cmp(tol).intValue();
    if((cmpVal == -1) || (!strict && (cmpVal != 1))) return new Integer(0);
    return cmp();
  }
  
  public Integer softCmp(int    tol, boolean strict){return softCmp(new HFloat(tol), strict);}
  public Integer softCmp(String tol, boolean strict){return softCmp(new HFloat(tol), strict);}
  
  
  public Integer softCmp(HFloat arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(HFloat arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(HFloat arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(int    arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, HFloat tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, int    tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  public Integer softCmp(String arg, String tol, boolean strict){return sbt(arg).softCmp(tol, strict);}
  
  public HFloat unFrac(){
    Integer i = toInteger();
    if(i == null) return NaN;
    return new HFloat(i.intValue());
  }

  public HFloat frac(){return sbt(unFrac());}

  public HFloat mod(HFloat arg){return div(arg).frac().mlt(arg);}
  public HFloat mod(int    arg){return div(arg).frac().mlt(arg);}
  public HFloat mod(String arg){return div(arg).frac().mlt(arg);}
  
  //Analysis
  
  public HFloat quad(){return mlt(this);}  
  
  public HFloat sqrt(){
    if(!this.valid) return NaN;
    int cmpInt = cmp().intValue();
    if(cmpInt == -1) return neg().sqrt();
    if(cmpInt ==  0) return new HFloat(0);
    HFloat altRet = new HFloat(2);
    HFloat ret    = new HFloat(1);
    HFloat arg    = get();
    int    mltMe  = 0;
    while(arg.cmp(1).intValue() == 1){
      ++mltMe;
      arg = arg.div(4);
    }
    for(;ret.cmp(altRet).intValue() == -1;){
    	altRet = ret;
    	ret    = (arg.add(ret.quad())).div(ret).div(2);
    }
    for(int mltLauf = 0; mltLauf < mltMe; ++mltLauf) ret = ret.mlt(2);
    return ret;
  }  
  
  public HFloat pow(HFloat arg){
    if(!this.valid) return NaN;
    if(cmp().intValue() == 0) return new HFloat(0);
    return ln().mlt(arg).exp();
  }
  
  public HFloat pow(int    arg){return pow(new HFloat(arg));}
  public HFloat pow(String arg){return pow(new HFloat(arg));}
  
  public HFloat exp(){
    if(!this.valid)               return NaN;
    if(cmp().intValue() < 0) return neg().exp().inv();  
    return taylor(new HFloatHTaylor(EXP_HTAYLOR));
    
  }
  
  public HFloat ln(){
    if(!this.valid) return NaN;
    int cmpInt = cmp().intValue();
    if(cmpInt ==  0) return NaN;
    if(cmpInt == -1) return neg().ln();
    HFloat arg     = this;
    HFloat zuKlein = new HFloat("0.5");
    HFloat zuGross = new HFloat("1.5");
    int addMe = 0;
    while(arg.cmp(zuKlein).intValue() == -1){
      --addMe;
      arg = arg.mlt(EUL);
    }
    while(arg.cmp(zuGross).intValue() == 1){
      ++addMe;
      arg = arg.div(EUL);
    }
    arg = arg.sbt(1);
    return arg.taylor(new HFloatHTaylor(LN_HTAYLOR)).mlt(arg).add(addMe);
  }

  public HFloat sin(){
    if(!this.valid) return NaN;
    HFloat arg = mod(PI.mlt(2));
    if(arg.cmp(PI       ).intValue() > 0) return arg.sbt(PI).sin().neg();
    if(arg.cmp(PI.div(2)).intValue() > 0) return PI.sbt(arg).sin();
    return arg.quad().taylor(new HFloatHTaylor(SIN_HTAYLOR)).mlt(arg);
  }
  
  public HFloat cos(){
    if(!this.valid) return NaN;
    HFloat arg = mod(PI.mlt(2));
    if(arg.cmp(PI       ).intValue() > 0) return arg.sbt(PI).cos().neg();
    if(arg.cmp(PI.div(2)).intValue() > 0) return PI.sbt(arg).cos().neg();
    return arg.quad().taylor(new HFloatHTaylor(COS_HTAYLOR));
  }
  
  public HFloat tan(){return sin().div(cos());}
  
  public HFloat cot(){return cos().div(sin());}
  
  public HFloat asin(){
    if(!this.valid                              ) return NaN;
    if(softCmp(0, 1, false).intValue() != 0) return NaN;
    if(cmp(               ).intValue() <  0) return neg().asin().neg();
    if(cmp("0.71"         ).intValue() >  0) return (new HFloat(1)).sbt(quad()).sqrt().acos();
    return mlt(quad().taylor(new HFloatHTaylor(ASIN_HTAYLOR)));
  }
  
  public HFloat acos(){return PI.div(2).sbt(asin());}
  
  public HFloat atan(){
    if(!this.valid) return NaN;
    if(cmp(     ).intValue() < 0) return neg().atan().neg();
    if(cmp(1    ).intValue() > 0) return PI.div(2).sbt(acot());
    if(cmp("0.5").intValue() > 0) return sbt(1).div(add(1)).atan().add(PI.div(4));
    return mlt(quad().taylor(new HFloatHTaylor(ATAN_HTAYLOR)));
  }
  
  public HFloat acot(){return inv().atan();}

  public HFloat sinh(){return exp().sbt(neg().exp()).div(2);}
  
  public HFloat cosh(){return exp().add(neg().exp()).div(2);}
  
  public HFloat tanh(){return sinh().div(cosh());}
  
  public HFloat coth(){return cosh().div(sinh());}

  public HFloat asinh(){return add(quad().add(1).sqrt()).ln();}
  
  public HFloat acosh(){return add(quad().sbt(1).sqrt()).ln();}
  
  public HFloat atanh(){return (add(1).div(sbt(1))).ln().div(2);}
  
  public HFloat acoth(){return (add(1).div(sbt(1))).ln().div(2);}

  //Hilf (protected)
  
  protected void HFloatInit(int wertA, int expoA, boolean validA){
	  this.mant  = wertA;
	  this.expo  = expoA;
	  this.valid = validA;
	  norm();
  }

  protected int[] format(long arg){
    int[] retval = new int[2];
    int   expoR  = 0;
    long  wertR  = arg;
    for(; abs(wertR) > iMax; wertR /= 10) expoR++;
    retval[0] = (int) wertR;
    retval[1] = expoR;
    return retval;
  }
  
  protected int integerParseInt(String text){
    int retval = 0;
    for(int n = 0; n < text.length(); ++n) retval = 10 * retval + "0123456789".indexOf(text.charAt(n)); 
    return retval;
  }

  protected String[] fuehrende(String text, char zch0, boolean auch){
    String[]       ret    = new String[2];
    StringBuffer[] retBuf = new StringBuffer[2];
    boolean        habe   = false;
    retBuf[0]             = new StringBuffer();
    retBuf[1]             = new StringBuffer();
    for(int wo = 0; wo < text.length(); ++wo){
      char zch1 = text.charAt(wo);
      if(zch0 == zch1){
        if(habe){
          if(auch) retBuf[0].append(zch1);
        }else{
          retBuf[1].append(zch1);
        }
      }else{
        habe = true;
        retBuf[0].append(zch1);
      }
    }
    ret[0] = retBuf[0].toString();
    ret[1] = retBuf[1].toString();
    return ret;
  }

  protected String[] stringSplit2(String text, char pat){
    String[]       ret    = new String[2];
    StringBuffer[] retBuf = new StringBuffer[2];
    int            habe   = 0;
    retBuf[0]             = new StringBuffer();
    retBuf[1]             = new StringBuffer();
    for(int wo = 0; wo < text.length(); ++wo){
      char zch = text.charAt(wo);
      if(zch == pat){
        ++habe;
      }else{
        if(habe < 2) retBuf[habe].append(zch);
      }
    }
    ret[0] = retBuf[0].toString();
    ret[1] = retBuf[1].toString();
    return ret;
  }

  protected int[] format(String text){
    int[]        retval  = new int[2];
    String[]     vorNach = new String[2];
    StringBuffer sb      = new StringBuffer();
    for(int wo = 0; wo < text.length(); ++wo){
      char zch = text.charAt(wo);
      if("0123456789e.-".indexOf(zch) == -1){
        if(zch == 'E') sb.append('e');
        if(zch == ',') sb.append('.');
      }else{
        sb.append(zch);
      }   
    }
    text                    = sb.toString();
    vorNach                 = stringSplit2(text, 'e');
    String strVorE          = vorNach[0];           
    String strNachE         = vorNach[1];           
    vorNach                 = fuehrende(strVorE, '-', false);
    String strMant          = fuehrende(vorNach[0], '0', true)[0];
    int    sgnMant          = 1 - 2 * (vorNach[1].length() % 2);
    vorNach                 = stringSplit2(strMant, '.');
    String strMantVorKomma  = vorNach[0];
    String strMantNachKomma = vorNach[1];
    vorNach                 = fuehrende(strNachE, '-', false);
    String strExpo          = fuehrende(vorNach[0], '0', true)[0];
    int    sgnExpo          = 1 - 2 * (vorNach[1].length() % 2);
    String strExpoVorKomma  = stringSplit2(strExpo, '.')[0];
    String mantStr;
    int    mantExpo;
    if("".equals(strMantVorKomma)){
      vorNach  =  fuehrende(strMantNachKomma, '0', true);
      mantStr  =  vorNach[0];
      mantExpo = -vorNach[1].length();
    }else{
      mantStr  = strMantVorKomma + strMantNachKomma;
      mantExpo = strMantVorKomma.length();
    }
    for(int n = 0; n < eInt; ++n) mantStr = mantStr + '0';
    mantStr  = mantStr.substring(0, eInt);
    int localMant = sgnMant * integerParseInt(mantStr);
    int localExpo = sgnExpo * integerParseInt(strExpoVorKomma) + mantExpo - eInt;
    retval[0] = localMant;
    retval[1] = localExpo;
    return retval;      
  } 
  
  protected void norm(){
    if(this.mant == 0){
    	this.expo = 0;
    }else{
      while(abs(this.mant) < iMin){
    	  this.expo--;
    	  this.mant *= 10;
      }
    }
  }
  
  protected int abs(int i){
    if(i < 0) return -i;
    else      return  i;
  }
  
  protected long abs(long i){
    if(i < 0) return -i;
    else      return  i;
  }

//Taylor

  public HFloat taylor(HTaylor hTaylor){
    if(!this.valid) return NaN;
    HFloat summe      = new HFloat(0);
    HFloat xN         = new HFloat(1);
    HFloat altSumme;
    for(int index = 0;; ++index){
      altSumme = summe;
      summe    = summe.add(xN.mlt(hTaylor.coeff(index)));
      xN       = xN.mlt(this);
      if(summe.cmp(altSumme).intValue() == 0) return summe;
    }
  }  
  
//Kreis- und Kugelkoordinaten
  
  public static HFloat[] polByCrt(HFloat xP, HFloat yP){
    HFloat[] retval = new HFloat[2];
    HFloat phi      = new HFloat();
    HFloat radi     = new HFloat();
    if(xP.valid && yP.valid){
      radi = xP.quad().add(yP.quad()).sqrt();
      int xCmp = xP.cmp().intValue();
      int yCmp = yP.cmp().intValue();
      switch(xCmp){
        case 1: 
          phi = yP.div(xP).atan();
          break;
        case 0: 
          phi = PI.div(2).mlt(yCmp);
          break;
        case -1: 
          phi = yP.div(xP).atan();
          phi = phi.add(HFloat.PI.mlt(-phi.cmp().intValue()));
          break;
      }
    }
    retval[0] = phi;
    retval[1] = radi;
    return retval;
  }

  public static HFloat[] crtByPol(HFloat phi, HFloat radi){
    HFloat[] retval = new HFloat[2];
    HFloat xP       = new HFloat();
    HFloat yP       = new HFloat();
    if(phi.valid && radi.valid){
      xP = radi.mlt(phi.cos());
      yP = radi.mlt(phi.sin());
    }
    retval[0] = xP;
    retval[1] = yP;
    return retval;
  }

  public static HFloat[] sphByCrt(HFloat xP, HFloat yP, HFloat zP){
    HFloat[] retval = new HFloat[3];
    HFloat bet      = new HFloat();
    HFloat lam      = new HFloat();
    HFloat radi     = new HFloat();
    if(xP.valid && yP.valid && zP.valid){
      HFloat[] pbcXY    = polByCrt(xP      , yP);
      HFloat[] pbcXYZ   = polByCrt(pbcXY[1], zP);
      lam               = pbcXY[0];
      bet               = pbcXYZ[0];
      radi              = pbcXYZ[1];
    }
    retval[0] = bet;
    retval[1] = lam;
    retval[2] = radi;
    return retval;
  }

  public static HFloat[] crtBySph(HFloat bet, HFloat lam, HFloat radi){
    HFloat[] retval = new HFloat[3];
    HFloat xP       = new HFloat();
    HFloat yP       = new HFloat();
    HFloat zP       = new HFloat();
    if(bet.valid && lam.valid && radi.valid){
      HFloat radiCosBet = radi.mlt(bet.cos());
      xP                = radiCosBet.mlt(lam.cos());
      yP                = radiCosBet.mlt(lam.sin());
      zP                = radi.mlt(bet.sin());
    }
    retval[0] = xP;
    retval[1] = yP;
    retval[2] = zP;
    return retval;
  }

//Const

  protected static final long    lMax  =             1000000000000000000L;
  protected static final int     iMax  =                      1000000000 ;
  protected static final int     iMin  =                       100000001 ;
  protected static final int     eInt  =                               9 ;
  protected static final int     eLong =                              18 ;
  public    static final HFloat  PI    = new HFloat("3.1415926535897931");
  public    static final HFloat  EUL   = new HFloat("2.7182818284590451");
  public    static final HFloat  NaN   = new HFloat("NaN"               );

//Member

  public int     mant ;
  public int     expo ;
  public boolean valid;
  
  
//Enum

  protected static final int ASIN_HTAYLOR = 0;
  protected static final int ATAN_HTAYLOR = 1;
  protected static final int COS_HTAYLOR  = 2;
  protected static final int EXP_HTAYLOR  = 3;
  protected static final int LN_HTAYLOR   = 4;
  protected static final int SIN_HTAYLOR  = 5;

}
                                                                                                                              