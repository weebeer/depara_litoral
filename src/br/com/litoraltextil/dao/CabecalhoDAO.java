package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class CabecalhoDAO {

	public static DynamicVO get(BigDecimal nuNota) throws Exception {
		JapeWrapper tgfCab = JapeFactory.dao("CabecalhoNota");
		DynamicVO tgfCabVO = null;
		tgfCabVO = tgfCab.findByPK(nuNota);
		return tgfCabVO;
	}

	public static DynamicVO getOneAdicional(BigDecimal instanciaPrincipal) throws Exception {
		JapeWrapper tgfCab = JapeFactory.dao("AD_TGFCAB");
		DynamicVO tgfCabVO = null;
		tgfCabVO = tgfCab.findOne("IDINSTPRN = ?",instanciaPrincipal);
		return tgfCabVO;
	}

	public static DynamicVO getOneFlow(BigDecimal nuNota) throws Exception {
		JapeWrapper tgfCab = JapeFactory.dao("AD_TGFCAB");
		DynamicVO tgfCabVO = null;
		tgfCabVO = tgfCab.findOne("NUNOTA = ?",nuNota);
		return tgfCabVO;
	}


	public static DynamicVO getID() throws Exception {
		JapeWrapper tgfnum = JapeFactory.dao("ControleNumeracao");
		DynamicVO tgfnumVO = null;
		tgfnumVO = tgfnum.findOne("ARQUIVO = ?", "AD_LOGTRANSFEMP");
		return tgfnumVO;
	}


	public static boolean existeDevolucaoProducao(BigDecimal nuNota) throws Exception {
		DynamicVO tgfCab = JapeFactory.dao("CabecalhoNota").findOne("NUNOTA = ? AND CODTIPOPER = 1220 AND STATUSNFE = 'A'",nuNota);
		if(tgfCab != null) {
			return true;
		} else {
			return false;
		}
	}

	public static BigDecimal getNunotaReq(BigDecimal id) throws Exception {
		try {
			JapeWrapper tgfCab = JapeFactory.dao("CabecalhoNota");
			DynamicVO tgfCabID = tgfCab.findOne("AD_IDTRANSFILIAIS = ? AND CODTIPOPER IN (1070,1320)", id);
			if (tgfCabID == null) {
				throw new Exception("Nota não encontrada para o ID: " + id);
			}
			return tgfCabID.asBigDecimal("NUNOTA");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}