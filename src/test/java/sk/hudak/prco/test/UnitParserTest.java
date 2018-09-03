package sk.hudak.prco.test;

import org.junit.Assert;
import org.junit.Test;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.UnitData;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.parser.impl.UnitParserImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jan.hudak on 9/21/2017.
 */
public class UnitParserTest {

    @Test
    public void parseTest() {
        Map<String, UnitData> values = new HashMap<>();

        // KUS
        addTestData(values, "PAMPERS Active Baby 4 MAXI 174ks (8-14kg), MESAČNÁ ZÁSOBA - jednorazové plienky",
                Unit.KUS, "174");
        addTestData(values, "PAMPERS Active Baby 4 MAXI 174 ks (8-14kg), MESAČNÁ ZÁSOBA - jednorazové plienky",
                Unit.KUS, "174");
        addTestData(values, "Pampers Fresh Clean Čistiace Obrúsky 4x64 Kusov",
                Unit.KUS, "64", 4);
        addTestData(values, "Pampers Premium Care Detské Jednorazové Plienky, Veľkosť 4 (Maxi) 8 - 14 kg, 52 Kusov",
                Unit.KUS, "52");
        addTestData(values, "Pampers Obrúsky Natural Clean 6×64 ks",
                Unit.KUS, "64", 6);
        addTestData(values, "Pampers Active Baby 6 Extra large 15 + kg, 56 ks GIANTPACK",
                Unit.KUS, "56");
        addTestData(values, "Pampers Active Baby 3 Midi (4-9kg) Giant Box -108ks",
                Unit.KUS, "108");
         addTestData(values, "Pampers Active Baby 4+ Maxi (9-16kg) Giant Pack - 70ks",
                Unit.KUS, "70");
         addTestData(values, "pampers pants veľ. 4 (352 ks) – dvojmesačná zásoba",
                Unit.KUS, "352");
         addTestData(values, "1x136 ks",
                Unit.KUS, "136");
         addTestData(values, "Pampers Active Baby-Dry Giant Cube Plus 5 (11-18kg) 78ks",
                Unit.KUS, "78");
         addTestData(values, "PAMPERS NEW BABY DRY 2 MINI 100KS",
                Unit.KUS, "100");

        // OBJEM
        addTestData(values, "Becherovka Original Bylinný likér 0,5 l",
                Unit.LITER, "0.5");
        addTestData(values, "Becherovka Original Bylinný likér 0,5l",
                Unit.LITER, "0.5");   //absolut vodka 40% 1x1 l
        addTestData(values, "Absolut vodka 40% 1x1 l",
                Unit.LITER, "1");
        addTestData(values, "Absolut vodka 40% 1x700 ml",
                Unit.LITER, "0.70000");
        addTestData(values, "Velkopopovický Kozel pivo tmavé 6x4x500 ml PLECH",
                Unit.LITER, "0.50000", 24);
//        addTestData(values, "absolut vodka 40% 1x1 l",
//                Unit.LITER, "1", 1);

        // VAHA
        addTestData(values, "Radoma Preto Filé z Aljašskej tresky 4 x 100 g",
                Unit.KILOGRAM, "0.10000", 4);
        addTestData(values, "Radoma Preto Filé z Aljašskej tresky 4 x 100g",
                Unit.KILOGRAM, "0.10000", 4);
        addTestData(values, "NUTRILON NUTRIMAMA Profutura cereálne tyčinky Brusnice a Maliny (5x40g)",
                Unit.KILOGRAM, "0.04000", 5);
        addTestData(values, "Nutrilon 4 Pronutra - 6×800g + 6 × 800g + vlhčené utierky Oncle SENSITIVE",
                Unit.KILOGRAM, "0.80000", 6);
        addTestData(values, "Nutrilon 4 Pronutra BiB CSR batoľacia mliečna výživa v prášku3x800 g",
                Unit.KILOGRAM, "0.80000", 3);
        addTestData(values, "6x nutrilon 4 pronutra (800g) - dojčenské mlieko",
                Unit.KILOGRAM, "0.80000", 6);
        addTestData(values, "nutrilon 4 pronutra (800 g) - dojčenské mlieko",
                Unit.KILOGRAM, "0.80000");
        addTestData(values, "2x nutrilon nutrimama profutura cereálne tyčinky brusnice a čokoláda (5x40g)",
                Unit.KILOGRAM, "0.04000", 10);
        addTestData(values, "nutrilon 4 3x800g xmass",
                Unit.KILOGRAM, "0.80000", 3);
        addTestData(values, "Nutrilon 4 Pronutra - 6×800g + vlhčené utierky Oncle SENSITIVE",
                Unit.KILOGRAM, "0.80000", 6);
        addTestData(values, "Nutrilon 4 Pronutra 800g AKCIA 5+1 ks zdarma",
                Unit.KILOGRAM, "0.80000", 6);
        addTestData(values, "Babičkina Voľba Múka polohrubá pšeničná 1 kg",
                Unit.KILOGRAM, "1");
        addTestData(values, "Kinder Chocolate tyčinky z mliečnej čokolády s mliečnou náplňou 8 x 12,5 g",
                Unit.KILOGRAM, "0.01250", 8);
         addTestData(values, "Nutrilon Nutrilon 4 Pronutra - 6 × 800g + vlhčené obrúsky Oncle SENSITIVE",
                Unit.KILOGRAM, "0.80000", 6);
         addTestData(values, "Nutrilon 4 3x600 g",
                Unit.KILOGRAM, "0.60000", 3);
         addTestData(values, "Nutrilon dojčenské mlieko 1 Pronutra Good Sleep 6x 800g",
                Unit.KILOGRAM, "0.80000", 6);
         addTestData(values, "2x800 g - mliečna výživa, 1x1 set",
                Unit.KILOGRAM, "0.80000", 2);
         addTestData(values, "Nutrilon 5 detská mliečna výživa v prášku 800 g 5+1 zdarma",
                Unit.KILOGRAM, "0.80000", 6);

        for (String name : values.keySet()) {
            verify(name, values.get(name));
        }
    }

    private void addTestData(Map<String, UnitData> values, String productName, Unit unit, String unitValue) {
        addTestData(values, productName, unit, new BigDecimal(unitValue), 1);
    }

    private void addTestData(Map<String, UnitData> values, String productName, Unit unit, BigDecimal unitValue) {
        addTestData(values, productName, unit, unitValue, 1);
    }

    private void addTestData(Map<String, UnitData> values, String productName, Unit unit, String unitValue, int unitPackageCount) {
        addTestData(values, productName, unit, new BigDecimal(unitValue), unitPackageCount);
    }

    private void addTestData(Map<String, UnitData> values, String productName, Unit unit, BigDecimal unitValue, int unitPackageCount) {
        values.put(productName, new UnitData(unit, unitValue, unitPackageCount));
    }

    private void verify(String name, UnitData expected) {
        Optional<UnitTypeValueCount> unitTypeValueCount = new UnitParserImpl().parseUnitTypeValueCount(name);
        Assert.assertTrue("unit type value is null", unitTypeValueCount.isPresent());
        Assert.assertEquals(expected.getUnit(), unitTypeValueCount.get().getUnit());
        Assert.assertEquals(expected.getUnitValue(), unitTypeValueCount.get().getValue());
        Assert.assertEquals(expected.getUnitPackageCount(), unitTypeValueCount.get().getPackageCount());
    }
}
