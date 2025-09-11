package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class UsuarioDAO {

	public static DynamicVO get(BigDecimal codusu) throws Exception {
		JapeWrapper tsiusu = JapeFactory.dao("Usuario");
		DynamicVO tsiusuVO = null;
		tsiusuVO = tsiusu.findByPK(codusu);
		return tsiusuVO;
	}
}