package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class ItemNotaDAO {

	public static DynamicVO get(BigDecimal nuNota,BigDecimal sequencia) throws Exception {
		JapeWrapper tgfite = JapeFactory.dao("ItemNota");
		DynamicVO tgfiteVO = null;
		tgfiteVO = tgfite.findByPK(nuNota,sequencia);
		return tgfiteVO;
	}

}