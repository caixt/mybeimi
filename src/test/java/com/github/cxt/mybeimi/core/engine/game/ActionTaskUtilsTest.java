package com.github.cxt.mybeimi.core.engine.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import com.alibaba.fastjson.JSONObject;
import com.github.cxt.mybeimi.core.engine.game.ActionTaskUtils;
import com.github.cxt.mybeimi.core.engine.game.CardType;
import com.github.cxt.mybeimi.util.rules.model.Player;
import com.github.cxt.mybeimi.util.rules.model.TakeCards;
import com.github.cxt.mybeimi.util.rules.model.TakeDiZhuCards;

public class ActionTaskUtilsTest {

	
	public static List<String> CARDS = Arrays.asList("3,4,5,6,7,8,9,10,J,Q,K,A,2,小王,大王".split(","));
//	                                                  0 1 2 3 4 5 6 7  8 9 101112 13 14
	
	
	static public byte[] formate(String crads){
		List<Byte> byteList = new ArrayList<>();
		for(int i = 0; i < crads.length(); ){
			int index = i;
			String temp = crads.substring(index, ++i);
			int byteIndex = CARDS.indexOf(temp);
			if(byteIndex != -1){
				byte b = (byte) (byteIndex);
				byteList.add(b);
				continue;
			}
			if(++i <= crads.length()){
				temp = crads.substring(index, i);
				byteIndex = CARDS.indexOf(temp);
				if(byteIndex != -1){
					byte b = (byte) (byteIndex);
					byteList.add(b);
					continue;
				}
			}
			throw new RuntimeException("crads:" + crads);
		}
		
		List<Byte> formateList = new ArrayList<>();
		for(byte b : byteList){
			if(b > 12){
				b = (byte) (b - 12 - 1 + (12 + 1) * 4);
				if(formateList.contains(b)){
					throw new RuntimeException("crads:" + crads);
				}
				formateList.add(b);
			}
			else {
				b = (byte) (b * 4);
				boolean add = false;
				for(int j = 0; j < 4; j++){
					byte t = (byte) (b + j);
					if(!formateList.contains(t)){
						formateList.add(t);
						add = true;
						break;
					}
				}
				if(!add){
					throw new RuntimeException("crads:" + crads);
				}
			}
		}
		
		Collections.sort(formateList);
		Collections.reverse(formateList);
		byte[] bytes = new byte[formateList.size()];
		for(int i = 0; i < formateList.size(); i++){
			bytes[i] = formateList.get(i);
		}
		return bytes;
	}
	
	
	//BMDataContext.CardsTypeEnum
	/*
	ONE(1),		//单张      3~K,A,2
	TWO(2),		//一对	 3~K,A,2
	THREE(3),	//三张	 3~K,A,2
	FOUR(4),	//三带一	 AAA+K
	FORMTWO(41),	//三带对	 AAA+K
	FIVE(5),	//单顺	连子		10JQKA
	SIX(6),		//双顺	连对		JJQQKK
	SEVEN(7),	//三顺	飞机		JJJQQQ
	EIGHT(8),	//飞机	带翅膀	JJJ+QQQ+K+A
	EIGHTONE(81),	//飞机	带翅膀	JJJ+QQQ+KK+AA
	NINE(9),	//四带二			JJJJ+Q+K
	NINEONE(91),	//四带二对			JJJJ+QQ+KK
	TEN(10),	//炸弹			JJJJ
	ELEVEN(11);	//王炸			0+0
	*/
	@Test
	public void test1(){
		byte[] cards = null;
		CardType cardType = null;
		
		cards = formate("J");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
				
		cards = formate("1010");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("333");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("3334");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("33344");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("34567");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("334455");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("333444");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("33344456");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("3334445566");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("333345");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("JJJJ4455");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("JJJJ");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
		
		cards = formate("小王大王");
		cardType = ActionTaskUtils.identification(cards);
		System.out.println(JSONObject.toJSON(cardType));
	}
	
	
	@Test
	public void test2(){
		CardType cardType2 = ActionTaskUtils.identification(formate("大王"));
		CardType cardType1 = ActionTaskUtils.identification(formate("小王"));
		System.out.println(ActionTaskUtils.allow(cardType2, cardType1));
	}
	
	
	
	@Test
	public void test3(){
		TakeDiZhuCards takeDiZhuCards = new TakeDiZhuCards();
		Player player = new Player("id");
		player.setCards(formate("33355小王大王"));
		byte[] b = takeDiZhuCards.getAIMostSmall(player, 0);
		System.out.println(b.length);
	}
	
	
	@Test
	public void test4(){
		TakeDiZhuCards takeDiZhuCards = new TakeDiZhuCards();
		Player player = new Player("id");
		player.setCards(formate("3344999"));
		
		TakeCards takeCards = new TakeDiZhuCards();
		takeCards.setCardType(ActionTaskUtils.identification(formate("8883")));
		byte[] b = takeDiZhuCards.search(player, takeCards);
		if(b != null){
			System.out.println(b.length);
		}
	}
				
}
