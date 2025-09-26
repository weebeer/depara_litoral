package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class ProdutoDAO {

	public static DynamicVO get(BigDecimal codprod) throws Exception {
		JapeWrapper tgfpro = JapeFactory.dao("Produto");
		DynamicVO tgfproVO = null;
		tgfproVO = tgfpro.findByPK(codprod);
		return tgfproVO;
	}

	public static DynamicVO getPantone(BigDecimal codcor) throws Exception {
		JapeWrapper pantone = JapeFactory.dao("PANTONE");
		DynamicVO pantoneVO = null;
		pantoneVO = pantone.findOne("CODCOR = ?", codcor);
		return pantoneVO;
	}


}