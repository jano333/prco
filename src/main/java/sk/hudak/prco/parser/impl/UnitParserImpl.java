package sk.hudak.prco.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.Unit;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.parser.UnitParser;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static sk.hudak.prco.utils.CalculationUtils.recalculateToKilograms;
import static sk.hudak.prco.utils.CalculationUtils.recalculateToLites;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

/**
 * Created by jan.hudak on 9/21/2017.
 */
@Slf4j
@Component
public class UnitParserImpl implements UnitParser {

    // INDEX pre group match je od JEDNA !!!!! nie nula

    private static final String NUMBER_AT_LEAST_ONE = "[0-9]{1,}";
    public static final String SPACE = " ";

    @Override
    public Optional<UnitTypeValueCount> parseUnitTypeValueCount(String productName) {
        notNullNotEmpty(productName, "productName");
        log.debug("parsing product name to retrieve unit info: {}", productName);

//        // TODO remove log...
//        log.debug("hash " + productName.hashCode());
//        char[] chars = productName.toCharArray();
//        for (char hh : chars) {
//            System.out.println("hh"+hh);
//            String s = hh + "";
//            System.out.println(s + ", hashCode: " + s.hashCode());
//        }
//        System.out.println(chars);


        productName = productName.toLowerCase();

        // --- KUS --

        //"PAMPERS Active Baby 4 MAXI 174ks (8-14kg), MESAČNÁ ZÁSOBA - jednorazové plienky"
        //"PAMPERS Active Baby 4 MAXI 174 ks (8-14kg), MESAČNÁ ZÁSOBA - jednorazové plienky"
        Matcher matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "ks| ks", SPACE);
        if (matcher.find()) {
            return createKus(matcher.group(2), "1");
        }
        // "Pampers Fresh Clean Čistiace Obrúsky 4x64 Kusov"
        // "Pampers Obrúsky Natural Clean 6×64 ks"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x|×", NUMBER_AT_LEAST_ONE, SPACE, "kusov|ks");
        if (matcher.find()) {
            return createKus(matcher.group(4), matcher.group(2));
        }
        // "Pampers Premium Care Detské Jednorazové Plienky, Veľkosť 4 (Maxi) 8 - 14 kg, 52 Kusov"
        // "Pampers Active Baby 6 Extra large 15 + kg, 56 ks GIANTPACK"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, SPACE, "kusov|ks");
        if (matcher.find()) {
            return createKus(matcher.group(2), "1");
        }
        // "Pampers Active Baby 3 Midi (4-9kg) Giant Box -108ks"
        // "Pampers Active Baby 4+ Maxi (9-16kg) Giant Pack - 70ks"
        matcher = craeteMatcher(productName, " -|-| - ", NUMBER_AT_LEAST_ONE, "kusov|ks");
        if (matcher.find()) {
            return createKus(matcher.group(2), "1");
        }

        // pampers pants veľ. 4 (352 ks) – dvojmesačná zásoba
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, " ks\\)");
        if (matcher.find()) {
            return createKus(matcher.group(1), "1");
        }
        // Pampers Active Baby-Dry Giant Cube Plus 5 (11-18kg) 78ks
        matcher = craeteMatcher(productName, "\\) ", NUMBER_AT_LEAST_ONE, "ks");
        if (matcher.find()) {
            return createKus(matcher.group(2), "1");
        }
        // 1x136 ks
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, " ks");
        if (matcher.find()) {
            return createKus(matcher.group(3), matcher.group(1));
        }

        //pampers new baby dry 2 mini 100ks
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "ks");
        if (matcher.find()) {
            return createKus(matcher.group(2));
        }

        //174 ks
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, SPACE, "ks$");
        if (matcher.find()) {
            return createKus(matcher.group(1));
        }

        //152 ks, (9 - 16 kg)
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, SPACE, "ks, ");
        if (matcher.find()) {
            return createKus(matcher.group(1));
        }

        // 'Pampers Baby Vlhčené obrúsky Sensitive 9x56ks'
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, "ks");
        if (matcher.find()) {
            return createKus(matcher.group(4), matcher.group(2));
        }

        // 'pampers jednorázové plienky activebaby mega box s5 110 pcs'
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, SPACE, "pcs");
        if (matcher.find()) {
            return createKus(matcher.group(2));
        }


        // --- OBJEM ---

        // Velkopopovický Kozel pivo tmavé 6x4x500 ml PLECH
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, SPACE, "ml");
        if (matcher.find()) {
            return createObjem(recalculateToLites(convertToBigDecimal(matcher.group(5))),
                    String.valueOf(Integer.valueOf(matcher.group(1)) * Integer.valueOf(matcher.group(3))));
        }

        // Lovela white prací gél 50 praní 1x4,7 l
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, ",", NUMBER_AT_LEAST_ONE, SPACE, "l");
        if (matcher.find()) {
            return createObjem(convertToBigDecimal(matcher.group(4) + matcher.group(5) + matcher.group(6)),
                    matcher.group(2));
        }


        // Lovela 2x Gél biela, 4,7 l / 50 pracích dávok
//        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", SPACE, "[a-z]*", SPACE);//, "[a-z]{1,}");//,",",SPACE,   NUMBER_AT_LEAST_ONE/*, ",", NUMBER_AT_LEAST_ONE, " l"*/);
//        if (matcher.find()) {
//            String group = matcher.group(4);
//
//            return createObjem(recalculateToLites(convertToBigDecimal(matcher.group(3))), matcher.group(1));
//        }

        // "Becherovka Original Bylinný likér 0,5 l"
        // "Becherovka Original Bylinný likér 0,5l"
        matcher = craeteMatcher(productName, SPACE, "[0-9]{1,},[0-9]{1,}", " l|l");
        if (matcher.find()) {
            return createObjem(matcher.group(2), "1");
        }
        // Velkopopovický Kozel 10% pivo výčapné svetlé 500 ml
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, " ml|ml");
        if (matcher.find()) {
            return createObjem(recalculateToLites(convertToBigDecimal(matcher.group(2))), "1");
        }
        // Palma Raciol Nízkoerukový repkový olej 1 l
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, " l|l");
        if (matcher.find()) {
            return createObjem(matcher.group(2), "1");
        }
        // Absolut vodka 40% 1x1 l
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, " l|l");
        if (matcher.find()) {
            return createObjem(matcher.group(3), matcher.group(1));
        }

        // Absolut vodka 40% 1x700 ml
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, " ml|ml");
        if (matcher.find()) {
            return createObjem(recalculateToLites(convertToBigDecimal(matcher.group(3))), matcher.group(1));
        }

        // --- VAHA ---
        // "2x nutrilon nutrimama profutura cereálne tyčinky brusnice a čokoláda (5x40g)"
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", ".{1,}", SPACE, "\\(", NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, "g\\)");
        if (matcher.find()) {
            String group = matcher.group(1);
            String group2 = matcher.group(6);
            String group3 = matcher.group(8);
            return createKilogram(recalculateToKilograms(convertToBigDecimal(group3)),
                    "" + (Integer.valueOf(group) * Integer.valueOf(group2)));
        }
        // "Radoma Preto Filé z Aljašskej tresky 4 x 100 g"
        // "Radoma Preto Filé z Aljašskej tresky 4 x 100g"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, " ", "x|×", " ", NUMBER_AT_LEAST_ONE, " g|g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(6))), matcher.group(2));
        }
        // "NUTRILON NUTRIMAMA Profutura cereálne tyčinky Brusnice a Maliny (5x40g)"
        matcher = craeteMatcher(productName, " \\(", NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, "g\\)");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(4))), matcher.group(2));
        }
        // "Nutrilon 4 Pronutra - 6×800g + 6 × 800g + vlhčené utierky Oncle SENSITIVE"
        // "Nutrilon 4 Pronutra - 6×800g + vlhčené utierky Oncle SENSITIVE"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "×", NUMBER_AT_LEAST_ONE, " g|g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(4))), matcher.group(2));
        }
        // "Nutrilon 4 Pronutra BiB CSR batoľacia mliečna výživa v prášku3x800 g"
        matcher = craeteMatcher(productName, SPACE, "\\D{1,}", NUMBER_AT_LEAST_ONE, "x", "[0-9]{1,4}", "g| g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(5))), matcher.group(3));
        }
        // "6x nutrilon 4 pronutra (800g) - dojčenské mlieko"
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", ".{1,}", SPACE, "\\(", NUMBER_AT_LEAST_ONE, " g\\)|g\\)");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(6))), matcher.group(1));
        }
        // "nutrilon 4 pronutra (800 g) - dojčenské mlieko"
        matcher = craeteMatcher(productName, SPACE, "\\(", NUMBER_AT_LEAST_ONE, " g\\)|g\\)");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(3))), "1");
        }
        // "nutrilon 4 3x800g xmass"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, "g ");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(4))), matcher.group(2));
        }
        // "Nutrilon 4 Pronutra 800g AKCIA 5+1 ks zdarma"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "g", SPACE, ".{1,}", SPACE, "[0-9]{1}", "\\+", "[0-9]{1}", SPACE, "ks");
        if (matcher.find()) {
            return createKilogram(
                    recalculateToKilograms(convertToBigDecimal(matcher.group(2))),
                    "" + (Integer.valueOf(matcher.group(7)) + Integer.valueOf(matcher.group(9))));
        }
        // "Babičkina Voľba Múka polohrubá pšeničná 1 kg"
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, SPACE, "kg");
        if (matcher.find()) {
            return createKilogram(convertToBigDecimal(matcher.group(2)), "1");
        }
        // Kinder Chocolate tyčinky z mliečnej čokolády s mliečnou náplňou 8 x 12,5 g
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, SPACE, "x", SPACE, NUMBER_AT_LEAST_ONE + ",[0-9]{1,2}", SPACE, "g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(5))), matcher.group(1));
        }
        // Nutrilon 4 3x600 g
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, SPACE, "g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(4))), matcher.group(2));
        }
        // Nutrilon dojčenské mlieko 1 Pronutra Good Sleep 6x 800g
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, "x", SPACE, NUMBER_AT_LEAST_ONE, "g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(5))), matcher.group(2));
        }
        // 2x800 g - mliečna výživa, 1x1 set
        matcher = craeteMatcher(productName, NUMBER_AT_LEAST_ONE, "x", NUMBER_AT_LEAST_ONE, " g|g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(3))), matcher.group(1));
        }
        // Nutrilon 5 detská mliečna výživa v prášku 800 g 5+1 zdarma
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, SPACE, "g", SPACE, NUMBER_AT_LEAST_ONE, "\\+", NUMBER_AT_LEAST_ONE, SPACE);
        if (matcher.find()) {
            String group = matcher.group(2);
            String group1 = matcher.group(6);
            String group2 = matcher.group(8);
            return createKilogram(recalculateToKilograms(
                    convertToBigDecimal(group)),
                    String.valueOf((Integer.valueOf(group1) + Integer.valueOf(group2)))
            );
        }

        // Hamé Májka Lahôdkový bravčový krém 75 g
        matcher = craeteMatcher(productName, SPACE, NUMBER_AT_LEAST_ONE, " g|g");
        if (matcher.find()) {
            return createKilogram(recalculateToKilograms(convertToBigDecimal(matcher.group(2))));
        }

        // LOVELA Color 3,25 kg (26 dávok) - prací prášok
        matcher = craeteMatcher(productName, SPACE, "[0-9]{1,},[0-9]{1,}", " kg|kg", SPACE);
        if (matcher.find()) {
            return createKilogram(convertToBigDecimal(matcher.group(2)));
        }

        log.warn("unit info not found for '{}'", productName);

        return Optional.empty();
    }

    private Matcher craeteMatcher(String productName, String... groups) {
        return cratePattern(groups).matcher(productName);
    }

    private Pattern cratePattern(String... groups) {
        StringJoiner stringJoiner = new StringJoiner(")(");
        for (String group : groups) {
            stringJoiner.add(group);
        }
        return Pattern.compile("(" + stringJoiner.toString() + ")");
    }

    private Optional<UnitTypeValueCount> createKus(String value) {
        return createKus(value, "1");
    }

    private Optional<UnitTypeValueCount> createKus(String value, String packageCount) {
        return createKus(convertToBigDecimal(value), packageCount);
    }

    private Optional<UnitTypeValueCount> createKus(BigDecimal value, String packageCount) {
        return Optional.of(new UnitTypeValueCount(Unit.KUS, value, Integer.valueOf(packageCount)));
    }

    private Optional<UnitTypeValueCount> createObjem(String value, String packageCount) {
        return createObjem(convertToBigDecimal(value), packageCount);
    }

    private Optional<UnitTypeValueCount> createObjem(BigDecimal value, String packageCount) {
        return Optional.of(new UnitTypeValueCount(Unit.LITER, value, Integer.valueOf(packageCount)));
    }

    private Optional<UnitTypeValueCount> createKilogram(BigDecimal value) {
        return createKilogram(value, "1");
    }

    private Optional<UnitTypeValueCount> createKilogram(BigDecimal value, String packageCount) {
        return Optional.of(new UnitTypeValueCount(Unit.KILOGRAM, value, Integer.valueOf(packageCount)));
    }


}
