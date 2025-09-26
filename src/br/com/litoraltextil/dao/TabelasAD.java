package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.util.Collection;

public class TabelasAD {

    public static DynamicVO CtrMtsDAO(BigDecimal nunota , BigDecimal codProdAtual) throws Exception {
        JapeWrapper ctrMts = JapeFactory.dao("AD_CTRMTS");
        DynamicVO ctrMtsVO = null;
        ctrMtsVO = ctrMts.findOne("NUNOTA = ? AND CODPROD = ?", nunota, codProdAtual);
        return ctrMtsVO;
    }

    public static DynamicVO sortProdDAO(BigDecimal codProj , BigDecimal codProdAtual) throws Exception {
        JapeWrapper sortProd = JapeFactory.dao("AD_SORTPROD");
        DynamicVO sortProdVO = null;
        sortProdVO = sortProd.findOne("CODPROJ = ? AND CODPRODSON = ?", codProj, codProdAtual);
        return sortProdVO;
    }

    public static DynamicVO deParaProdDAO(BigDecimal codprod) throws Exception {
        JapeWrapper dePara = JapeFactory.dao("AD_DEPARA");
        DynamicVO deParaVO = null;
        deParaVO = dePara.findOne("CODPRODINS = ?", codprod);
        return deParaVO;
    }

    public static DynamicVO deParaIdDAO(BigDecimal id) throws Exception {
        JapeWrapper dePara = JapeFactory.dao("AD_DEPARA");
        DynamicVO deParaVO = null;
        deParaVO = dePara.findOne("ID = ?", id);
        return deParaVO;
    }

    public static Collection<DynamicVO> deParaAllidDAO() throws Exception {
        JapeWrapper dePara = JapeFactory.dao("AD_DEPARA");
        Collection<DynamicVO> deParaVO = null;
        deParaVO = dePara.find("ID >= ?", BigDecimal.ZERO);
        return deParaVO;
    }

    public static Collection<DynamicVO> deParaAllCodProjDAO(BigDecimal codproj) throws Exception {
        JapeWrapper dePara = JapeFactory.dao("AD_DEPARA");
        Collection<DynamicVO> deParaVO = null;
        deParaVO = dePara.find("CODPROJ = ?", codproj);
        return deParaVO;
    }

    public static DynamicVO familiaProdDAO(BigDecimal codprod) throws Exception {
        JapeWrapper familiaProd = JapeFactory.dao("FAMGR3");
        DynamicVO familiaProdVO = null;
        familiaProdVO = familiaProd.findOne("CODPRODGR3 = ?", codprod);
        return familiaProdVO;
    }

    public static DynamicVO OrdDeParaDAO(BigDecimal codprod) throws Exception {
        JapeWrapper ordDePara = JapeFactory.dao("AD_ORDDEPARA");
        DynamicVO ordDeParaVO = null;
        ordDeParaVO = ordDePara.findOne("COPRODANT = ?", codprod);
        return ordDeParaVO;
    }


}
