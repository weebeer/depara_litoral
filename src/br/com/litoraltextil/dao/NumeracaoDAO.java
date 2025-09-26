package br.com.litoraltextil.dao;

import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import org.apache.bcel.generic.RETURN;

import java.math.BigDecimal;
import java.sql.ResultSet;

public class NumeracaoDAO {

	public static BigDecimal get(String tabela) throws Exception {
		JapeWrapper tgfnum = JapeFactory.dao("ControleNumeracao");
		DynamicVO tgfnumVO = null;
		tgfnumVO = tgfnum.findOne("ARQUIVO = ?", tabela);
		BigDecimal num = tgfnumVO.asBigDecimal("ULTCOD").add(BigDecimal.ONE);
		return num;
	}
	public static BigDecimal getDeParaID() throws Exception {
		JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
		BigDecimal id = null;
		try{
			NativeSql sql = new NativeSql(jdbc);
			sql.appendSql("SELECT ISNULL(MAX(ID)+1,1) AS ID FROM AD_DEPARA");
			ResultSet rs = sql.executeQuery();
			rs.next();
			id = rs.getBigDecimal("ID");
			return id;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			jdbc.closeSession();
		}
    }
	public static BigDecimal getDeParaCOD() throws Exception {
		JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
		BigDecimal cod = null;
		try{
			NativeSql sql = new NativeSql(jdbc);
			sql.appendSql("SELECT ISNULL(MAX(CODDEPARA)+1,1) AS CODDEPARA FROM AD_DEPARA");
			ResultSet rs = sql.executeQuery();
			rs.next();
			cod = rs.getBigDecimal("CODDEPARA");
			return cod;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			jdbc.closeSession();
		}
    }
	public static BigDecimal getLogDeParaID() throws Exception {
		JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
		BigDecimal id = null;
		try{
			NativeSql sql = new NativeSql(jdbc);
			sql.appendSql("SELECT ISNULL(MAX(ID)+1,1) AS ID FROM AD_LOGDEPARA");
			ResultSet rs = sql.executeQuery();
			rs.next();
			id = rs.getBigDecimal("ID");
			return id;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			jdbc.closeSession();
		}
    }
	public static BigDecimal getOrdDeParaID() throws Exception {
		JdbcWrapper jdbc = EntityFacadeFactory.getDWFFacade().getJdbcWrapper();
		BigDecimal id = null;
		try{
			NativeSql sql = new NativeSql(jdbc);
			sql.appendSql("SELECT ISNULL(MAX(ID)+1,1) AS ID FROM AD_ORDDEPARA");
			ResultSet rs = sql.executeQuery();
			rs.next();
			id = rs.getBigDecimal("ID");
			return id;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			jdbc.closeSession();
		}
    }
}