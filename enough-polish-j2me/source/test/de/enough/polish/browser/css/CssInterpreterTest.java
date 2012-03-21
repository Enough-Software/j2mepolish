//#condition polish.usePolishGui
/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */

package de.enough.polish.browser.css;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;

import junit.framework.TestCase;

/**
 * Tests the CssReader
 * 
 * @author robertvirkus
 */
public class CssInterpreterTest extends TestCase {

	public CssInterpreterTest(String name) {
		super(name);
	}

	public void xtestNextToken() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + "focused { background        {\ncolor: green;/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: blue\n; }"
				 + "title { text-effect: shadow;\n \ntext-shadow-color: green;      }"
		);
		int i = 0;
		String[] tokens = new String[]{
			".myName",
			"color: #fff",
			"background-color: #333",
			"",
			"focused",
			"background",
			"color: green",
			"image: url(/test.png)",
			"type: image",
			"",
			"font-color: blue",
			"",
			"title",
			"text-effect: shadow",
			"text-shadow-color: green",
			""
		};
		StringBuffer strBuffer = new StringBuffer();
		while (reader.hasNextToken()) {
			String token = reader.nextToken( strBuffer );
			assertEquals( tokens[i], token );
			i++;
			if (i > 50) {
				fail("Unable to parse page");
			}
		}
		
		assertNull( reader.nextToken(strBuffer));
	}
	
	public void xtestNextStyle() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + "focused { background        {\ncolor: rgb( 10, 100 % , 255 );/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: rgb( 10, 100 % , 255 )\n; }"
				 + "title { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    #33ee22   }"
				 + "test { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    green   }"
				 + "test2 { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    transparent   }"
		);
		Style style = reader.nextStyle();
		assertEquals( "myname", style.name);
		Color color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0xffffff, color.getColor() );
		
		style = reader.nextStyle();
		assertEquals( "focused", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( (10 << 16) | (255 << 8) | 255, color.getColor() );
		
		style = reader.nextStyle();
		assertEquals( "title", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x33ee22, color.getColor() );

		style = reader.nextStyle();
		assertEquals( "test", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x008000, color.getColor() );

		style = reader.nextStyle();
		assertEquals( "test2", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( Color.TRANSPARENT, color.getColor() );

		style = reader.nextStyle();
		assertNull( style );
	}
	
	
	public void xtestFocusedStyle() throws IOException {
		CssInterpreter reader = new CssInterpreter("/****** here are some more comments; }{;;;;} .mystyle{} ******  */\n"
				 + ".myName { color: #fff; background-color: #333\n }\n"
				 + ".myName:hover { background        {\ncolor: rgb( 10, 100 % , 255 );/**** another comment / another test */ image: url(/test.png); type: image              }\nfont-color: rgb( 10, 100 % , 255 )\n; }"
				 + ".myName:pressed { text-effect: shadow;\n \ntext-shadow-color: #0f0;   \n\n font-color:    #33ee22   }"
		);
		Hashtable styles = reader.getAllStyles();
		Style style = (Style) styles.get("myname");
		Style origStyle = style;
		assertNotNull(style);
		assertEquals( "myname", style.name);
		Color color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0xffffff, color.getColor() );
		
		style = (Style) origStyle.getObjectProperty("focused-style");
		assertNotNull(style);
		assertEquals( "mynamefocused", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( (10 << 16) | (255 << 8) | 255, color.getColor() );
		
		style = (Style) origStyle.getObjectProperty("pressed-style");
		assertNotNull(style);
		assertEquals( "mynamepressed", style.name);
		color = style.getColorProperty("font-color");
		assertNotNull(color);
		assertEquals( 0x33ee22, color.getColor() );

	}
	
	public void xtestFacebookCss() {
		String cssCode = "/*<![CDATA[*/body{background:#fff;color:#333;font-family:\"lucida grande\",tahoma,verdana,arial,sans-serif;font-size: 11px;margin:0;padding:0}\n"
				+ "a{color:#3b5998;text-decoration:none}\n"
				+ "div{margin:0;padding:0}\n"
				+ "ul{list-style-type:none;margin:0;padding:0}\n"
				+ "h2{font-size: 13px;margin:0;padding:0}\n"
				+ ".clearfix:after{clear:both;content:\".\";display:block;font-size:0;height:0;line-height:0;visibility:hidden}\n"
				+ ".connect_stripes{height:10px;background:url(https://s-static.ak.facebook.com/rsrc.php/v1/y7/r/sVrwJtmBVjI.gif) repeat-x}\n"
				+ "#header{background-color:#6d84b4;color:#fff;font-size: 14px;padding:4px}\n"
				+ ".title{font-weight:bold}\n"
				+ ".platform_dialog_content_padded{padding:15px 10px 20px 10px}\n"
				+ ".platform_dialog_error,\n"
				+ ".platform_dialog_status{padding-bottom:15px}\n"
				+ ".platform_dialog_bottom_bar{background:#f2f2f2;border-bottom:1px solid #ccc;border-top:1px solid #ccc}\n"
				+ ".platform_dialog_bottom_bar_table{border:0;border-collapse:collapse;border-spacing:0;width:100%}\n"
				+ ".platform_dialog_buttons{padding:10px}\n"
				+ ".uiButton{border:1px solid #999;display:inline-block;padding:2px 6px}\n"
				+ ".uiButton + .uiButton{margin-left:4px}\n"
				+ ".uiButton input{background:none;border:0;color:#333;font-weight:bold;margin:0;padding:1px 0 2px}\n"
				+ ".uiButtonConfirm{background-color:#5b74a8;border-color:#29447e #29447e #1a356e}\n"
				+ ".uiButtonConfirm input{color:#fff}\n"
				+ ".UIMessageBox{padding:10px;border-width:1px;border-style:solid}\n"
				+ ".UIMessageBox .sub_message{margin:4px 0 0}\n"
				+ ".status{background-color:#fff9d7;border-color:#e2c822}\n"
				+ ".error{background-color:#ffebe8;border-color:#dd3c10}\n"
				+ ".error a{color:#dd3c10}\n"
				+ ".acw{background-color:#fff}\n"
				+ ".acbk{background-color:#000}\n"
				+ ".acb{background-color:#3b5998}\n"
				+ ".aclb{background-color:#eceff5}\n"
				+ ".acg{background-color:#f2f2f2}\n"
				+ ".acy{background-color:#fffbe2}\n"
				+ ".acr{background-color:#ffebe8}\n"
				+ "body{margin:0;padding:0;text-align:left;direction:ltr}\n"
				+ "body, tr, input, textarea, button{font-family:sans-serif}\n"
				+ ".fcb{color:#000}\n"
				+ ".fcg{color:gray}\n"
				+ ".fcw{color:#fff}\n"
				+ ".fcl{color:#3b5998}\n"
				+ ".fcs{color:#6d84b4}\n"
				+ ".mfsxs{font-size:x-small}\n"
				+ ".mfss{font-size:small}\n"
				+ "body, tr, input, textarea, .mfsm{font-size:medium}\n"
				+ ".mfsl{font-size:large}\n"
				+ "/*]]>*/";
		CssInterpreter reader = new CssInterpreter(cssCode);
		Hashtable styles = reader.getAllStyles();
		//System.out.println("styles.size()=" + styles.size());
		Enumeration enumeration = styles.keys();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			//System.out.println("name: [" + name + "], style=" + styles.get(name));
		}
	}
	
	public void testFacebookCss2() {
		String cssCode = "#mErrorView .image{background-repeat:no-repeat;background-position:center center;background-image:url(data:image/png;base64,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);display:inline-block;height:150px;width:170px}\n";

		String cssCode2 = "/*<![CDATA[*/.mobile-login-field{width:90%}\n"
				+ "/* @noflip */\n"
				+ ".mobile-login-field-email{direction:ltr}\n"
				+ "form{margin:0;border:0}\n"
				+ ".acw{background-color:#fff}\n"
				+ ".acbk{background-color:#000}\n"
				+ ".acb{background-color:#3b5998}\n"
				+ ".aclb{background-color:#eceff5}\n"
				+ ".acg{background-color:#f2f2f2}\n"
				+ ".acy{background-color:#fffbe2}\n"
				+ ".acr{background-color:#ffebe8}\n"
				+ ".touch .aps{padding:2px 8px}\n"
				+ ".touch .apm{padding:5px 8px}\n"
				+ ".touch .apl{padding:8px 8px}\n"
				+ ".fcb{color:#000}\n"
				+ ".fcg{color:gray}\n"
				+ ".fcw{color:#fff}\n"
				+ ".fcl{color:#3b5998}\n"
				+ ".fcs{color:#6d84b4}\n"
				+ ".mfsxs{font-size:x-small}\n"
				+ ".mfss{font-size:small}\n"
				+ "body, tr, input, textarea, .mfsm{font-size:medium}\n"
				+ ".mfsl{font-size:large}\n"
				+ ".touch .mfsxs{font-size: 10px;line-height:12px}\n"
				+ ".touch .mfss{font-size: 12px;line-height:15px}\n"
				+ "body, tr, input, textarea, .touch .mfsm{font-size: 14px;line-height:18px}\n"
				+ ".touch .mfsl{font-size: 16px;line-height:20px}\n"
				+ ".input{border:solid 1px #999;border-top-color:#888;margin:0;padding:3px}\n"
				+ ".touch .input{-webkit-appearance:none;-webkit-border-radius:0;-webkit-box-shadow:inset 0 1px 0 rgba(0, 0, 0, .07);-webkit-box-sizing:border-box;padding:4px 7px 5px}\n"
				+ ".touch textarea.input{padding:4px 5px 5px}\n"
				+ ".android .input{-webkit-box-shadow:inset 0 2px 1px -1px rgba(0, 0, 0, .15);padding-top:5px}\n"
				+ ".btn{border:solid 2px;cursor:pointer;margin:0;padding:2px 6px 3px;text-align:center}\n"
				+ ".largeBtn{display:block;width:100%}\n"
				+ ".btnForm{display:inline;border:none;padding:0}\n"
				+ ".btnD,\n"
				+ ".acb .btnC,\n"
				+ ".btnI{background:#f3f4f5;border-color:#ccc #aaa #999;color:#505c77}\n"
				+ ".acb .btnD,\n"
				+ ".btnC,\n"
				+ ".acb .btnI{background:#5b74a8;border-color:#8a9ac5 #29447E #1a356e;color:#fff}\n"
				+ ".btnS{background:#69a74e;border-color:#98c37d #3b6e22 #2c5115;color:#fff}\n"
				+ ".btnN{background:#ee3f10;border-color:#f48365 #8d290e #762610;color:#fff}\n"
				+ ".btn,\n"
				+ ".btnForm{display:inline-block}\n"
				+ ".btn + .btn,\n"
				+ ".btnForm + .btnForm,\n"
				+ ".btn + .btnForm,\n"
				+ ".btnForm + .btn{margin-left:3px}\n"
				+ ".largeBtn + .largeBtn{margin-left:0;margin-top:6px}\n"
				+ ".btn input{background:none;border:none;margin:0;padding:0}\n"
				+ ".btnD input,\n"
				+ ".acb .btnC input,\n"
				+ ".btnI input{color:#505c77}\n"
				+ ".acb .btnD input,\n"
				+ ".btnC input,\n"
				+ ".acb .btnI input,\n"
				+ ".btnS input,\n"
				+ ".btnN input{color:#fff}\n"
				+ ".touch .btn{-webkit-background-clip:padding-box;border:solid 1px;-webkit-border-radius:4px;-webkit-box-sizing:border-box;display:inline-block;font-weight:bold;line-height:27px;min-width:50px;overflow:hidden;padding:0 8px;text-overflow:ellipsis;white-space:nowrap}\n"
				+ ".touch .btn .img{margin-right:4px}\n"
				+ ".touch .btn + .btn{margin-left:5px}\n"
				+ ".hires.touch .btn,.touch .btnC.bgb{border:none;padding:1px 9px}\n"
				+ ".touch .medBtn{padding:3px 8px 2px}\n"
				+ ".hires.touch .medBtn,.touch .btnC.bgb.medBtn{padding:4px 8px 3px}\n"
				+ ".touch .largeBtn{-webkit-border-radius:6px;padding:7px 16px}\n"
				+ ".hires.touch .largeBtn,.touch .btnC.bgb.largeBtn{padding:8px 17px}\n"
				+ ".touch .largeBtn + .largeBtn{margin-left:auto;margin-top:12px}\n"
				+ ".touch .btn.iconOnly{min-width:32px;padding-left:0;padding-right:0}\n"
				+ ".touch .btn.iconOnly .img{margin-right:0}\n"
				+ ".touch .btnD,.touch .btnI{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#fdfefe),\n"
				+ "to(#f0f1f2)\n"
				+ ");-webkit-box-shadow:inset 0 1px 0 #fff,\n"
				+ "0 1px 0 rgba(0, 0, 0, .08)}\n"
				+ ".touch .btnD,.touch .btnC.bgb,.touch .btnI{border-color:#ccc #c0c1c2 #bdbec0}\n"
				+ ".touch .btn.btnD,.touch .btn.btnC.bgb,.touch .btn.btnI{color:#505c77;text-shadow:0 1px 0 rgba(255, 255, 255, .6)}\n"
				+ ".hires.touch .btnD{-webkit-box-shadow:inset 0 0 1px rgba(0, 0, 0, .7),\n"
				+ "inset 0 1px 0 #fff,\n"
				+ "0 1px 2px -1px rgba(0, 0, 0, .25)}\n"
				+ ".touch .btnD.touched{-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .18),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnD.touched,.touch .btnI.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#edeeee),\n"
				+ "to(#e4e5e6)\n"
				+ ");border-color:#b0adae #bfbdbd #c7c8ca}\n"
				+ ".hires.touch .btnD.touched{-webkit-box-shadow:inset 0 0 1px rgba(0, 0, 0, .4),\n"
				+ "inset 0 1px 2px rgba(0, 0, 0, .25),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnD.bgb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#4663a2),\n"
				+ "to(#344f8e)\n"
				+ ");border-color:#3a4a7b #2f406f #2b3a69;-webkit-box-shadow:inset 0 1px 0 rgba(255, 255, 255, .1),\n"
				+ "0 1px 0 rgba(255, 255, 255, .08)}\n"
				+ ".touch .btn.btnD.bgb,.touch .btn.btnC,.touch .btn.btnI.bgb,.touch .btn.btnS,.touch .btn.btnN{color:#fff;text-shadow:0 -1px 0 rgba(0, 0, 0, .35)}\n"
				+ ".hires.touch .btnD.bgb{-webkit-box-shadow:inset 0 0 1px #000,\n"
				+ "inset 0 1px 0 rgba(255, 255, 255, .12),\n"
				+ "0 1px 0 rgba(255, 255, 255, .08)}\n"
				+ ".touch .btnD.bgb.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#31508e),\n"
				+ "to(#234180)\n"
				+ ");border-color:#213564 #20366b #243771;-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .25),\n"
				+ "0 1px 0 rgba(255, 255, 255, .1)}\n"
				+ ".hires.touch .btnD.bgb.touched{-webkit-box-shadow:inset 0 0 1px rgba(0, 0, 0, .4),\n"
				+ "inset 0 1px 2px rgba(0, 0, 0, .25),\n"
				+ "0 1px 0 rgba(255, 255, 255, .1)}\n"
				+ ".touch .btnI.bgdb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#3b4456),\n"
				+ "to(#242a3a)\n"
				+ ");border-color:#1b202a #1f2531 #1a1f2d;-webkit-box-shadow:inset 0 1px 0 rgba(0, 0, 0, .1),\n"
				+ "0 1px 1px rgba(255, 255, 255, .17);color:#bdc4d3;text-shadow:0 -1px 0 rgba(0, 0, 0, .35)}\n"
				+ ".touch .btnI.bgdb.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#2d3544),\n"
				+ "to(#1a1f2c)\n"
				+ ");border-color:#141720 #171c25 #101620;-webkit-box-shadow:inset 0 1px 0 rgba(0, 0, 0, .1),\n"
				+ "0 1px 1px rgba(255, 255, 255, .17);color:#bec5d4;text-shadow:0 -1px 0 rgba(0, 0, 0, .35)}\n"
				+ ".hires.touch .btnI.bgdb{-webkit-box-shadow:inset 0 2px 1px -1px rgba(0, 0, 0, .45),\n"
				+ "inset 0 0 1px rgba(0, 0, 0, 1.0),\n"
				+ "0 0 1px rgba(255, 255, 255, .1),\n"
				+ "0 1px 0 rgba(255, 255, 255, .07)}\n"
				+ ".hires.touch .btnI.bgdb.touched{-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .7),\n"
				+ "inset 0 0 1px rgba(0, 0, 0, .5),\n"
				+ "0 0 1px rgba(255, 255, 255, .1),\n"
				+ "0 1px 0 rgba(255, 255, 255, .07)}\n"
				+ ".touch .btnD.bglb{border-color:#cacaca #b8babf #b1b4ba}\n"
				+ ".touch .btnD.bglb,.touch .btnI.bglb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#fafbfe),\n"
				+ "to(#e0e3ea)\n"
				+ ")}\n"
				+ ".touch .btnD.bglb.touched{border-color:#a6a7ab #abaeb3 #aeb0b5}\n"
				+ ".touch .btnD.bglb.touched,.touch .btnI.bglb.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#e9ebf0),\n"
				+ "to(#cbced4)\n"
				+ ")}\n"
				+ ".touch .btnC{border-color:#576499 #3a4b73 #263855;-webkit-box-shadow:inset 0 1px 0 rgba(255, 255, 255, .17),\n"
				+ "0 1px 0 rgba(0, 11, 42, .12)}\n"
				+ ".touch .btnC,.touch .btnI.bgb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#647aab),\n"
				+ "to(#2c467e)\n"
				+ ")}\n"
				+ ".hires.touch .btnC,.hires.touch .btnS,.hires.touch .btnN{-webkit-box-shadow:inset 0 0 1px rgba(0, 0, 0, .7),\n"
				+ "inset 0 1px 0 rgba(255, 255, 255, .3),\n"
				+ "0 1px 2px -1px rgba(0, 0, 0, .7)}\n"
				+ ".touch .btnC.touched{border-color:#2f3c5e #24345d #1d305d}\n"
				+ ".touch .btnC.touched,.touch .btnS.touched,.touch .btnN.touched{-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .3),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnC.touched,.touch .btnI.bgb.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#495f8e),\n"
				+ "to(#1f355e)\n"
				+ ")}\n"
				+ ".hires.touch .btnC.touched,.hires.touch .btnS.touched,.hires.touch .btnN.touched{-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .45),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnC.bgb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#fafbfe),\n"
				+ "to(#cfd2d9)\n"
				+ ");-webkit-box-shadow:0 0 1px rgba(0, 0, 0, .4),\n"
				+ "0 1px 2px rgba(0, 0, 0, .4)}\n"
				+ ".touch .btnC.bgb.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#dfe1e8),\n"
				+ "to(#b9bcc4)\n"
				+ ");-webkit-box-shadow:0 0 1px rgba(0, 0, 0, .9),\n"
				+ "inset 0 1px 2px rgba(0, 0, 0, .35),\n"
				+ "0 1px 0 rgba(255, 255, 255, .17)}\n"
				+ ".hires.touch .btnI{-webkit-box-shadow:inset 0 1px 1px rgba(43, 48, 60, .39),\n"
				+ "inset 0 0 1px rgba(43, 48, 60, 1.0),\n"
				+ "0 1px 0 #fff}\n"
				+ ".hires.touch .btnI.touched{-webkit-box-shadow:inset 0 1px 2px rgba(43, 48, 60, .7),\n"
				+ "inset 0 0 3px rgba(43, 48, 60, .4),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnI.bgb{border-color:#2a3349 #2a3a64 #253662;-webkit-box-shadow:inset 0 1px 1px rgba(0, 0, 0, .21),\n"
				+ "0 1px 0 rgba(255, 255, 255, .17)}\n"
				+ ".touch .btnI.bgb.touched{border-color:#1b2438 #1a2745 #18294d;-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .6),\n"
				+ "0 1px 0 rgba(255, 255, 255, .17)}\n"
				+ ".hires.touch .btnI.bgb{-webkit-box-shadow:inset 0 2px 1px -1px rgba(0, 0, 0, .45),\n"
				+ "inset 0 0 1px rgba(0, 0, 0, 1.0),\n"
				+ "0 1px 0 rgba(255, 255, 255, .17)}\n"
				+ ".hires.touch .btnI.bgb.touched{-webkit-box-shadow:inset 0 1px 2px rgba(0, 0, 0, .7),\n"
				+ "inset 0 0 1px rgba(0, 0, 0, .5),\n"
				+ "0 1px 0 rgba(255, 255, 255, .17)}\n"
				+ ".touch .btnI{border-color:#9599a1 #a8abb4 #acb1bb;-webkit-box-shadow:inset 0 1px 1px rgba(43, 48, 60, .18),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnI.touched{border-color:#767a84 #92969f #a8abb4;-webkit-box-shadow:inset 0 1px 2px rgba(43, 48, 60, .58),\n"
				+ "0 1px 0 #fff}\n"
				+ ".touch .btnS{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#75ab4b),\n"
				+ "to(#4a8532)\n"
				+ ");border-color:#68954c #427329 #386a24}\n"
				+ ".touch .btnS,.touch .btnN{-webkit-box-shadow:inset 0 1px 0 rgba(255, 255, 255, .22),\n"
				+ "0 1px 0 rgba(0, 0, 0, .08)}\n"
				+ ".touch .btnS.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#4f8425),\n"
				+ "to(#225f0b)\n"
				+ ");border-color:#496f2d #46782b #45802c}\n"
				+ ".touch .btnN{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#df4451),\n"
				+ "to(#b91d2e)\n"
				+ ");border-color:#b3373e #9e232c #941723}\n"
				+ ".touch .btnN.touched{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#b00930),\n"
				+ "to(#8b000f)\n"
				+ ");border-color:#912c33 #ad2d37 #b41d2b}\n"
				+ ".touch .btn[disabled] .img,.touch .btn[disabled].touched .img{opacity:.5}\n"
				+ ".touch .btnD[disabled],.touch .btnC.bgb[disabled],.touch .btnI[disabled]{opacity:1;color:#a7abb5;text-shadow:none}\n"
				+ ".touch .btnD.bgb[disabled],.touch .btnC[disabled],.touch .btnI.bgb[disabled]{opacity:1;color:#9dabce;text-shadow:none}\n"
				+ ".touch .btnS[disabled]{opacity:1;color:#aecd9c;text-shadow:none}\n"
				+ ".touch .btnN[disabled]{opacity:1;color:#e4969c;text-shadow:none}\n"
				+ ".touch a{color:#576b95;-webkit-tap-highlight-color:rgba(128,128,128,0.5);text-decoration:none}\n"
				+ ".touch a.sub{color:gray}\n"
				+ ".touch a.sec{color:#8190b0}\n"
				+ ".touch a.inv{color:#fff;-webkit-tap-highlight-color:rgba(255,255,255,0.5)}\n"
				+ ".touchable{cursor:pointer}\n"
				+ "a.touchable{color:inherit;-webkit-tap-highlight-color:rgba(255,255,255,0)}\n"
				+ "#viewport{min-height:100%;overflow:hidden;position:relative;width:100%}\n"
				+ "#viewport,#root,body{-webkit-transition-property:height, min-height;-webkit-transition-duration:.1s;-webkit-transition-timing-function:ease}\n"
				+ "#page{position:relative}\n"
				+ ".touch .mFuturePageHeader + #root{-webkit-box-sizing:border-box;margin-top:-44px;padding-top:44px}\n"
				+ ".touch .mFuturePageHeader{position:relative;z-index:12}\n"
				+ ".touch .mFuturePageHeader table{border-collapse:collapse;height:29px;width:100%}\n"
				+ ".touch .mFuturePageHeader.titled table{table-layout:fixed}\n"
				+ ".touch .mFuturePageHeader td{padding:0;vertical-align:middle}\n"
				+ ".touch .mFuturePageHeader td.left{width:25%}\n"
				+ ".touch .mFuturePageHeader td.left .btn{position:relative;z-index:13}\n"
				+ ".touch .mFuturePageHeader td.center{min-width:160px;text-align:center;width:50%}\n"
				+ ".touch .mFuturePageHeader td.right{text-align:right;width:25%}\n"
				+ ".touch .mFuturePageHeader td.left .btn,.touch .mFuturePageHeader td.right .btn{max-width:85px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}\n"
				+ ".touch .mFuturePageHeader td.right .pageHeaderChromelessButton{float:right}\n"
				+ ".chromeBar{height:29px;padding:7px 5px}\n"
				+ ".chromeBar.acb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#738aba),\n"
				+ "to(#2c4987)\n"
				+ ");border-color:#111a33;-webkit-box-shadow:inset 0 1px 1px -1px #fff}\n"
				+ ".chromeBar.aclb{background:-webkit-gradient(\n"
				+ "linear,\n"
				+ "left top,\n"
				+ "left bottom,\n"
				+ "from(#fafbfe),\n"
				+ "to(#e2e5eb)\n"
				+ ");border-color:#b9bcc1;-webkit-box-shadow:inset 0 1px 1px -1px #fff}\n"
				+ ".abt{border-top:1px solid}\n"
				+ ".abb{border-bottom:1px solid}\n"
				+ ".acw{border-color:#e9e9e9}\n"
				+ ".acb{border-color:#1d4088}\n"
				+ ".aclb{border-color:#d8dfea}\n"
				+ ".acg{border-color:#ccc}\n"
				+ ".acy{border-color:#e2c822}\n"
				+ ".acr{border-color:#dd3c10}\n"
				+ ".viewportArea{height:100%;position:absolute;top:0;width:100%}\n"
				+ ".mCenteredLoader{text-align:center;width:100%}\n"
				+ ".mCenteredLoaderVertical{position:absolute;top:50%;margin-top:-19px}\n"
				+ ".mCenteredLoader .img{position:relative;right:5px;top:-1px}\n"
				+ "#mErrorView .container{font-family:\"Helvetica Neue\", sans-serif;margin-top:-110px;position:absolute;text-align:center;top:50%;width:100%}\n"
				+ "#mErrorView .image{background-repeat:no-repeat;background-position:center center;background-image:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKoAAACWCAMAAAB5EONmAAABwlBMVEXI0OL///////+srKyGkabY3upXWFkAAAC3u8PDy90AAAAAAADLy8y0u8y0u8ukqrqrssLl5eW8xNWsssK8w9XR0dIAAAD5+fm7xNWss8LJy9Hl5eakq7qrssHw7/ClqrrDyt3w8PDDy9za2tu7w9X4+fmrs8Kkqrv5+Pnb2tsAAADa29v+/v7z9PYAAADb29sAAADP09vT2OK7w9Tv8PDR0tLv7/DR0dMAAAClq7q8xNTDytzS0dK8w9RDRkisssMAAABER0lER0kAAAAAAAAAAAB6e3u8wcmRkpKrssPAxc2io6O0tLTEydFlZmdvcHHw7+8AAACOkplkZmi7xNQAAADa2txDRkg/QkTExMUAAAAAAACnqKmho6Ta29wAAAAdHh8XGBg8P0E9P0E2OToAAAAAAAAlJyhCRUcxNDU9P0G6ury6u7zS1+W2u8jq7PHN1OTS0tPf4OLb2tzDyNT5+Pilqrukq7ulq7uxtcH4+PnMztDS0tLLztOsrK1lZ2m4u8Wprr3w8O/5+fjT1uH4+fi6vsrP0tXIyMpnamuorbzR0tO+wcfZ3OTn5+fLysv6+/3OztDExsvW2N26wM5ER0nIpHWgAAAAlnRSTlOzswCzs7OzD7OzNiezs7Ozs7Ozs7OzALOzs7Ozs7Ozs7Ozs7Ozs7Ozs7MBs7OzBrMOs7Ozs7OzsyCzs7Ozs36zBBJ9AxgLs7Ozs7Ozs7Ozs7MKs7OzJrOoi7M1EbOzszEwPYyQYyskSahsi7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7NmtuQuAAADxUlEQVR42u3c+VPTaBgHcF2FUl1NTdILakKTXrTYFiilpSAop5zifd/33rr36u7qrsce3ub/3e9LbLZTZhh+YXw7fp+U533e502az0w600npdMsnLROkkkoqqaSSSurmUQ8cdGSMgwdWqaEGaXa/I2fszzZTxx1ZY7yZutLe3n5+q2xxHtSVZmoR1K3yBajFZqpPWqqPVFJJJbXh3b+Ruu1Dx1qq9+7fItRxpxWo3qU/0tYmNbXh0rfJTh13Woa6ggKXvqOjQ3pqEUVba1B9LUyVJkgllVRSSf1Q1H17tzfH3n3e6pbNj41TIV1rlZMK2doglVRSSSWVVFJJJZVUmam9vb2tchcgFXX9eyvZqOsHqaSSSiqppJJKKqmkkkoqqS1E5f9YSSWVVH4fgB8Fk0oqqaSSSiqppJJK6gapvAvYjO8D8DMrUkkllVRSSSWV1PWp/f39oCLLT+3r6wMVWX5qZ2fnzMwM8tLSkvzUubk55OXlZfmpi4uLyNPT05JTP63H7OzsxqSvNl/69frU+fn5ZPiPZCaceZwMI37OhMPfZZKoM9jEgCbG5Fd+v2X5azW/VfO70e3vttDEiMLrecmyahay1xddd+F90+pGYbkLNbHwBRg4dSaTbKSerlMXFhYM47ARLxtx45FhxEtxEWihjpfLpXg5jmbZKP2pR/SEqiciujpwbHRYjWBTdV1XVfRRD4zqA+gci2A/fTiSiKijWMIDu2A+ikZiADNkNFTMEngmrKiRYVR6Qk88N0pGqYyzv/CoJx3nHJT12NPVtQeBJLL7EFtXY+/tu127d+/CJgZk/L0v66O77q55C26JcBve8e5z1Hvujt/e7cI5xck+d5yTLvWU4xz97H9qocc+YffYhZ5cD3LBzhVs+0IuZ9to3MsVRDP34MtoIBBNHQogR1FqmjaWCmgYMUFPw0rqECZopFBGUaa0MewxpgWiAUyjmoa+OBBtHCFKzFOalsIy5k+e2uKUuRP2s78c59QqtXjNgfWc9xr4ZyQ48s1QMDgYHBwaHAqOBEVgFB2xBV+/+f2hophKOhZTMOLxI0ZRK2mRzLRZn6ODCntiqJoxxTRFGcOBMTONQ2Ooq6iVdFr53oyZ1ar5g1k1lb9/+veX++5ZX/76G3zXioI6demOI3vcuTQlqPnJm1ckl165OZkX1Alf5fLtq7ekdd66evtyxTchqNn82cr1i2d2yBpnLl6vnM27P7+ZzfsmKzeO75Qzjt+oTPry2dAqNZSdyE8VfbJGcSo/kQ2F+AO8pJJKKqkfMfU/hM/KaicmSuAAAAAASUVORK5CYII=);display:inline-block;height:150px;width:170px}\n"
				+ "#mErrorView .message{color:#60676f;font-size: 18px;font-weight:bold;margin:5px 0 15px}\n"
				+ "#mErrorView .link{color:#576b95;font-size: 16px;font-weight:bold;text-decoration:none}\n"
				+ ".img{border:0;display:inline-block;vertical-align:top}\n"
				+ "i.img u{position:absolute;top:-9999999px}\n"
				+ ".async_saving_show{display:none}\n"
				+ ".async_elem_saving .async_saving_show{display:inline}\n"
				+ ".async_elem_saving .async_saving_hide{display:none}\n"
				+ ".async_saving_visible{visibility:hidden}\n"
				+ ".async_elem_saving .async_saving_visible{visibility:visible}\n"
				+ "body{margin:0;padding:0;text-align:left;direction:ltr}\n"
				+ "body, tr, input, textarea, button{font-family:sans-serif}\n"
				+ "body.iphone,body.android{border-top:1px solid #1d4088}\n"
				+ "body.iframe,body.app,body.webapp{border-top:none}\n"
				+ ".touch,.touch td,.touch input,.touch textarea\n"
				+ ".touch button{font-family:Helvetica, sans-serif;font-size: 14px}\n"
				+ ".android,.android td,.android input,.android textarea,.android button{font-family:'Droid Sans', Helvetica, sans-serif}\n"
				+ ".hires.iphone,.hires.iphone td,.hires.iphone input,.hires.iphone textarea,.hires.iphone button{font-family:'Helvetica Neue', Helvetica, sans-serif}\n"
				+ "*{-webkit-touch-callout:none;-webkit-tap-highlight-color:rgba(0,0,0,0);-webkit-text-size-adjust:none;-webkit-user-select:none}\n"
				+ "input,textarea{-webkit-user-select:auto}\n"
				+ ".landscape .portrait_only{display:none !important}\n"
				+ ".portrait .landscape_only{display:none !important}\n"
				+ "/*]]>*/";
		CssInterpreter reader = new CssInterpreter(cssCode);
		Hashtable styles = reader.getAllStyles();
		System.out.println("styles.size()=" + styles.size());
		Enumeration enumeration = styles.keys();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			System.out.println("name: [" + name + "], style=" + styles.get(name));
		}

	}
	
	public void xtestFacebookCss3() {
		String cssCode = "/*<![CDATA[*/body{background:#fff;color:#333;font-family:\"lucida grande\",tahoma,verdana,arial,sans-serif;font-size: 11px;margin:0;padding:0}\n"
				+ "a{color:#3b5998;text-decoration:none}\n"
				+ "div{margin:0;padding:0}\n"
				+ "ul{list-style-type:none;margin:0;padding:0}\n"
				+ "h2{font-size: 13px;margin:0;padding:0}\n"
				+ ".clearfix:after{clear:both;content:\".\";display:block;font-size:0;height:0;line-height:0;visibility:hidden}\n"
				+ ".connect_stripes{height:10px;background:url(https://s-static.ak.facebook.com/rsrc.php/v1/y7/r/sVrwJtmBVjI.gif) repeat-x}\n"
				+ "#header{background-color:#6d84b4;color:#fff;font-size: 14px;padding:4px}\n"
				+ ".title{font-weight:bold}\n"
				+ ".platform_dialog_content_padded{padding:15px 10px 20px 10px}\n"
				+ ".platform_dialog_error,\n"
				+ ".platform_dialog_status{padding-bottom:15px}\n"
				+ ".platform_dialog_bottom_bar{background:#f2f2f2;border-bottom:1px solid #ccc;border-top:1px solid #ccc}\n"
				+ ".platform_dialog_bottom_bar_table{border:0;border-collapse:collapse;border-spacing:0;width:100%}\n"
				+ ".platform_dialog_buttons{padding:10px}\n"
				+ ".uiButton{border:1px solid #999;display:inline-block;padding:2px 6px}\n"
				+ ".uiButton + .uiButton{margin-left:4px}\n"
				+ ".uiButton input{background:none;border:0;color:#333;font-weight:bold;margin:0;padding:1px 0 2px}\n"
				+ ".uiButtonConfirm{background-color:#5b74a8;border-color:#29447e #29447e #1a356e}\n"
				+ ".uiButtonConfirm input{color:#fff}\n"
				+ ".UIMessageBox{padding:10px;border-width:1px;border-style:solid}\n"
				+ ".UIMessageBox .sub_message{margin:4px 0 0}\n"
				+ ".status{background-color:#fff9d7;border-color:#e2c822}\n"
				+ ".error{background-color:#ffebe8;border-color:#dd3c10}\n"
				+ ".error a{color:#dd3c10}\n"
				+ ".acw{background-color:#fff}\n"
				+ ".acbk{background-color:#000}\n"
				+ ".acb{background-color:#3b5998}\n"
				+ ".aclb{background-color:#eceff5}\n"
				+ ".acg{background-color:#f2f2f2}\n"
				+ ".acy{background-color:#fffbe2}\n"
				+ ".acr{background-color:#ffebe8}\n"
				+ "body{margin:0;padding:0;text-align:left;direction:ltr}\n"
				+ "body, tr, input, textarea, button{font-family:sans-serif}\n"
				+ ".fcb{color:#000}\n"
				+ ".fcg{color:gray}\n"
				+ ".fcw{color:#fff}\n"
				+ ".fcl{color:#3b5998}\n"
				+ ".fcs{color:#6d84b4}\n"
				+ ".mfsxs{font-size:x-small}\n"
				+ ".mfss{font-size:small}\n"
				+ "body, tr, input, textarea, .mfsm{font-size:medium}\n"
				+ ".mfsl{font-size:large}\n"
				+ "/*]]>*/";
		CssInterpreter reader = new CssInterpreter(cssCode);
		Hashtable styles = reader.getAllStyles();
		//System.out.println("styles.size()=" + styles.size());
		Enumeration enumeration = styles.keys();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			//System.out.println("name: [" + name + "], style=" + styles.get(name));
		}
	}
}
