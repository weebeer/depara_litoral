package br.com.litoraltextil.dao;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;

public class ParceiroDAO {

        public static DynamicVO get(BigDecimal codParc) throws Exception {

            JapeWrapper tgfPar = JapeFactory.dao("Parceiro");

            DynamicVO tgfParVO = null;

            tgfParVO = tgfPar.findByPK(codParc);

            return tgfParVO;

        }

        static DynamicVO getCGC(String cgc) throws Exception {

            JapeWrapper tgfPar = JapeFactory.dao("Parceiro");

            DynamicVO tgfParVO = null;

            tgfParVO = tgfPar.findOne("CGC_CPF = ?", cgc);

            return tgfParVO;

        }
}
