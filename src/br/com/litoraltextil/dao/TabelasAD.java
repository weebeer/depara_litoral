package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

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

    public static DynamicVO deParaDAO(BigDecimal codprod) throws Exception {
        JapeWrapper dePara = JapeFactory.dao("AD_DEPARA");
        DynamicVO deParaVO = null;
        deParaVO = dePara.findOne("CODPRODINS = ?", codprod);
        return deParaVO;
    }

}
