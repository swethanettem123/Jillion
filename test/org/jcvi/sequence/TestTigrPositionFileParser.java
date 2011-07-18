/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.io.IOException;

import org.jcvi.common.core.seq.EncodedShortGlyph;
import org.jcvi.common.core.seq.ShortGlyphFactory;
import org.jcvi.common.core.seq.pos.fasta.PositionDataStore;
import org.jcvi.common.core.seq.read.TigrPositionsFileParser;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTigrPositionFileParser {
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    String pathToPosfile = "files/example.pos";
    
    EncodedShortGlyph IWKNA07T07A12MP1027R = new EncodedShortGlyph(
            FACTORY.getGlyphsFor(new short[]{
                    7, 15, 28, 39, 61, 83, 100, 130, 153, 160, 176, 193, 203, 216, 
                    230, 238, 246, 262, 278, 293, 305, 318, 327, 339, 352, 365,
                    380, 395, 407, 416, 433, 445, 457, 466, 482, 495, 505, 518,
                    526, 538, 553, 567, 580, 594, 602, 613, 625, 634, 646, 661,
                     673, 682, 694, 704, 718, 731, 742, 754, 766, 779, 792, 806,
                      818, 831, 844, 856, 868, 880, 891, 900, 916, 926, 941, 953,
                      963, 974, 985, 995, 1008, 1021, 1032, 1044, 1057, 1068, 
                     1080, 1092, 1104, 1114, 1125, 1136, 1149, 1161, 1172, 1183, 
                     1196, 1206, 1219, 1232, 1244, 1255, 1265, 1278, 1290, 1303, 
                     1315, 1328, 1340, 1351, 1364, 1376, 1387, 1398, 1410, 1422, 
                     1433, 1446, 1456, 1468, 1480, 1492, 1504, 1517, 1529, 1542, 
                     1553, 1565, 1574, 1584, 1596, 1607, 1619, 1632, 1645, 1656, 
                     1668, 1680, 1692, 1702, 1714, 1727, 1740, 1751, 1764, 1776, 
                     1786, 1798, 1811, 1823, 1833, 1845, 1857, 1868, 1881, 1893, 
                     1904, 1916, 1929, 1941, 1954, 1966, 1978, 1990, 2002, 2013, 
                     2024, 2036, 2048, 2059, 2069, 2083, 2095, 2106, 2118, 2129, 
                     2141, 2153, 2166, 2178, 2188, 2200, 2213, 2223, 2234, 2246, 
                     2258, 2270, 2282, 2293, 2306, 2317, 2329, 2341, 2354, 2366, 
                     2378, 2389, 2400, 2413, 2426, 2436, 2448, 2460, 2472, 2483, 
                     2494, 2507, 2519, 2532, 2543, 2554, 2566, 2578, 2590, 2603, 
                     2615, 2628, 2641, 2652, 2663, 2675, 2686, 2697, 2709, 2721, 
                     2733, 2746, 2758, 2770, 2782, 2794, 2807, 2816, 2828, 2840, 
                     2850, 2864, 2876, 2888, 2901, 2913, 2924, 2935, 2948, 2960, 
                     2972, 2984, 2996, 3007, 3019, 3030, 3043, 3055, 3066, 3078, 
                     3089, 3101, 3113, 3125, 3137, 3147, 3159, 3171, 3183, 3196, 
                     3208, 3220, 3232, 3244, 3256, 3268, 3280, 3292, 3303, 3315, 
                     3327, 3338, 3350, 3362, 3374, 3386, 3398, 3409, 3421, 3432, 
                     3444, 3456, 3468, 3480, 3490, 3502, 3514, 3526, 3538, 3550, 
                     3562, 3572, 3584, 3595, 3607, 3619, 3632, 3644, 3657, 3669, 
                     3680, 3692, 3704, 3715, 3727, 3738, 3750, 3762, 3774, 3785, 
                     3798, 3810, 3823, 3836, 3847, 3859, 3871, 3883, 3895, 3907, 
                     3920, 3930, 3943, 3955, 3967, 3978, 3990, 4002, 4014, 4025, 
                     4037, 4049, 4061, 4074, 4086, 4098, 4110, 4122, 4134, 4145, 
                     4157, 4169, 4181, 4193, 4206, 4217, 4230, 4241, 4253, 4264, 
                     4276, 4287, 4299, 4311, 4324, 4336, 4348, 4360, 4372, 4385, 
                     4397, 4409, 4421, 4433, 4445, 4457, 4468, 4480, 4491, 4504, 
                     4516, 4527, 4539, 4550, 4562, 4575, 4587, 4599, 4610, 4622, 
                     4633, 4645, 4658, 4670, 4682, 4695, 4707, 4719, 4731, 4743, 
                     4756, 4767, 4779, 4791, 4803, 4815, 4828, 4839, 4850, 4861, 
                     4874, 4886, 4898, 4911, 4922, 4934, 4946, 4958, 4970, 4982, 
                     4995, 5006, 5017, 5030, 5042, 5054, 5065, 5077, 5089, 5102, 
                     5112, 5124, 5136, 5148, 5160, 5172, 5185, 5198, 5210, 5222, 
                     5234, 5246, 5259, 5271, 5282, 5294, 5307, 5318, 5329, 5342, 
                     5354, 5367, 5379, 5391, 5402, 5414, 5426, 5437, 5449, 5461, 
                     5472, 5485, 5497, 5509, 5522, 5534, 5547, 5559, 5571, 5583, 
                     5594, 5607, 5619, 5631, 5643, 5655, 5667, 5679, 5691, 5701, 
                     5714, 5725, 5738, 5750, 5763, 5775, 5788, 5798, 5811, 5823, 
                     5836, 5848, 5860, 5872, 5884, 5896, 5909, 5921, 5932, 5943, 
                     5955, 5967, 5978, 5991, 6004, 6016, 6028, 6040, 6052, 6065, 
                     6077, 6089, 6101, 6114, 6125, 6138, 6148, 6161, 6174, 6186, 
                     6198, 6210, 6222, 6234, 6247, 6259, 6272, 6285, 6296, 6307, 
                     6319, 6331, 6343, 6355, 6367, 6379, 6390, 6403, 6415, 6428, 
                     6440, 6453, 6465, 6476, 6487, 6498, 6510, 6523, 6535, 6547, 
                     6560, 6573, 6585, 6597, 6609, 6620, 6632, 6643, 6656, 6667, 
                     6680, 6693, 6705, 6717, 6728, 6740, 6752, 6763, 6776, 6788, 
                     6800, 6811, 6823, 6836, 6849, 6861, 6872, 6884, 6896, 6908, 
                     6922, 6933, 6945, 6956, 6967, 6979, 6991, 7003, 7016, 7028, 
                     7040, 7052, 7063, 7075, 7087, 7100, 7112, 7124, 7137, 7149, 
                     7161, 7173, 7184, 7195, 7207, 7218, 7231, 7243, 7256, 7268, 
                     7279, 7292, 7304, 7315, 7327, 7339, 7351, 7363, 7374, 7386, 
                     7399, 7411, 7424, 7436, 7448, 7460, 7471, 7483, 7495, 7508, 
                     7519, 7530, 7543, 7555, 7568, 7581, 7593, 7605, 7617, 7629, 
                     7639, 7651, 7664, 7675, 7687, 7700, 7713, 7724, 7736, 7749, 
                     7760, 7773, 7784, 7795, 7808, 7820, 7832, 7845, 7856, 7869, 
                     7881, 7893, 7904, 7915, 7927, 7939, 7950, 7962, 7974, 7987, 
                     7999, 8010, 8022, 8034, 8045, 8058, 8069, 8080, 8093, 8105, 
                     8117, 8129, 8141, 8153, 8165, 8178, 8190, 8200, 8212, 8223, 
                     8236, 8248, 8259, 8272, 8283, 8296, 8309, 8320, 8332, 8343, 
                     8354, 8366, 8378, 8391, 8403, 8415, 8427, 8438, 8451, 8463, 
                     8474, 8487, 8498, 8511, 8523, 8535, 8547, 8559, 8570, 8582, 
                     8594, 8606, 8619, 8631, 8642, 8655, 8667, 8679, 8693, 8704, 
                     8715, 8726, 8739, 8750, 8763, 8774, 8785, 8798, 8810, 8822, 
                     8834, 8845, 8857, 8869, 8881, 8894, 8904, 8918, 8931, 8941, 
                     8953, 8964, 8977, 8988, 9000, 9013, 9026, 9039, 9049, 9060, 
                     9073, 9085, 9097, 9108, 9120, 9132, 9144, 9156, 9168, 9179, 
                     9192, 9203, 9217, 9229, 9241, 9253, 9264, 9277, 9288, 9302, 
                     9314, 9324, 9337, 9349, 9361, 9372, 9384, 9394, 9406, 9419, 
                     9432, 9445, 9456, 9468, 9478, 9489, 9499, 9508, 9516, 9528, 
                     9542, 9553, 9565, 9578, 9589, 9601, 9615, 9627, 9636, 9649, 
                     9659, 9671, 9683, 9696, 9707, 9718, 9731, 9743, 9754, 9767, 
                     9778, 9789, 9799, 9812, 9825, 9836, 9847, 9860, 9873, 9884, 
                     9895, 9908, 9919, 9930, 9944, 9955, 9966, 9979, 9989, 10002,
                      10015, 10024, 10037, 10048, 10062, 10073, 10085, 10098, 10109,
                     10123, 10134, 10146, 10158, 10171, 10181, 10194, 10205, 
                     10217, 10230, 10242, 10253, 10265, 10275, 10288, 10298, 10309,
                      10323, 10335, 10348, 10358, 10369, 10380, 10394, 10405,
                     10417, 10428, 10440, 10453, 10463, 10473, 10486, 10499, 10510
                     , 10522, 10534, 10545, 10558, 10572, 10583, 10595, 10607,
                     10619, 10631, 10642, 10655, 10672, 10679, 10689, 10704, 10714,
                      10727, 10742, 10752, 10762, 10771, 10783, 10793, 10805, 10816,
                      10828, 10838, 10846, 10865, 10877, 10889, 10898, 10911, 
                     10922, 10933, 10945, 10956, 10968, 10979, 10992, 11003, 11015,
                     11028, 11038, 11049, 11061, 11072, 11083, 11099, 11107,
                     11120, 11132, 11143, 11157, 11167, 11179, 11193, 11202, 11215
                     , 11228, 11235, 11248, 11260, 11274, 11285, 11294, 11311,
                     11321, 11330, 11344, 11353, 11367, 11377, 11386, 11397, 11408,
                      11422, 11435, 11447, 11457, 11469, 11483, 11492, 11506,
                     11516, 11526, 11540, 11552, 11562, 11574, 11585, 11598, 11611, 
                     11625, 11632, 11646, 11655, 11667, 11677, 11692, 11699, 11709,
                     11725, 11738, 11747, 11759, 11772, 11785, 11798, 11807, 
                     11818, 11829, 11839, 11851, 11862, 11875, 11886, 11894, 11905
                     , 11919, 11926, 11938, 11947, 11961, 11973, 11979, 11990

                    
            })
        );
    
    EncodedShortGlyph IWKNA07T08G07MP461F = new EncodedShortGlyph(
            FACTORY.getGlyphsFor(new short[]{
                    6, 19, 32, 45, 65, 88, 102, 106, 133, 139, 157, 165, 176,
                     182, 195, 208, 216, 230, 243, 255, 269, 284, 297, 305, 319,
                     332, 353, 358, 373, 387, 397, 407, 421, 435, 451, 462, 475,
                     486, 498, 506, 516, 525, 539, 553, 564, 572, 584, 595, 606, 
                     621, 636, 646, 661, 674, 683, 692, 703, 715, 724, 736, 749, 
                     761, 770, 782, 795, 809, 821, 832, 844, 857, 868, 879, 891, 
                     903, 913, 927, 938, 949, 959, 970, 983, 995, 1008, 1021, 1032
                     , 1042, 1052, 1065, 1078, 1089, 1099, 1113, 1126, 1137, 1148
                     , 1158, 1171, 1185, 1199, 1209, 1222, 1231, 1244, 1256, 1268
                     , 1278, 1291, 1301, 1313, 1326, 1339, 1351, 1364, 1374, 1386
                     , 1398, 1411, 1422, 1434, 1444, 1456, 1468, 1481, 1495, 1506
                     , 1518, 1529, 1539, 1551, 1562, 1575, 1588, 1599, 1611, 1623
                     , 1636, 1647, 1660, 1672, 1682, 1693, 1706, 1718, 1730, 1743
                     , 1755, 1766, 1778, 1790, 1803, 1814, 1826, 1838, 1849, 1861
                     , 1873, 1886, 1897, 1910, 1921, 1934, 1945, 1956, 1968, 1982
                     , 1995, 2006, 2019, 2030, 2042, 2054, 2065, 2078, 2089, 2102
                     , 2114, 2126, 2137, 2149, 2162, 2173, 2186, 2196, 2209, 2222
                     , 2232, 2243, 2256, 2269, 2280, 2292, 2305, 2317, 2329, 2341
                     , 2352, 2364, 2377, 2389, 2400, 2412, 2424, 2436, 2449, 2461
                     , 2474, 2486, 2496, 2508, 2519, 2531, 2544, 2556, 2569, 2582
                     , 2595, 2605, 2617, 2629, 2640, 2652, 2664, 2676, 2689, 2700
                     , 2712, 2724, 2737, 2748, 2760, 2771, 2783, 2795, 2807, 2819
                     , 2832, 2844, 2856, 2868, 2880, 2892, 2902, 2914, 2925, 2937
                     , 2949, 2961, 2971, 2984, 2996, 3007, 3019, 3032, 3044, 3056
                     , 3068, 3080, 3093, 3105, 3116, 3127, 3139, 3149, 3161, 3173
                     , 3186, 3197, 3208, 3221, 3234, 3245, 3257, 3270, 3281, 3292
                     , 3304, 3315, 3328, 3340, 3352, 3364, 3375, 3388, 3400, 3413
                     , 3424, 3436, 3449, 3461, 3474, 3485, 3497, 3509, 3520, 3532
                     , 3544, 3557, 3568, 3580, 3592, 3603, 3616, 3628, 3640, 3653
                     , 3664, 3676, 3688, 3699, 3711, 3723, 3735, 3747, 3760, 3770
                     , 3783, 3795, 3807, 3819, 3832, 3844, 3856, 3868, 3880, 3891
                     , 3902, 3914, 3927, 3938, 3951, 3963, 3975, 3986, 3998, 4010
                     , 4022, 4033, 4044, 4056, 4068, 4079, 4092, 4105, 4118, 4131
                     , 4143, 4153, 4166, 4178, 4190, 4203, 4214, 4226, 4237, 4249
                     , 4262, 4274, 4287, 4298, 4310, 4321, 4333, 4346, 4358, 4371
                     , 4383, 4395, 4406, 4418, 4430, 4442, 4455, 4467, 4480, 4491
                     , 4503, 4516, 4528, 4540, 4552, 4564, 4576, 4588, 4600, 4612
                     , 4624, 4636, 4646, 4657, 4669, 4681, 4694, 4705, 4717, 4729
                     , 4741, 4754, 4765, 4777, 4789, 4802, 4814, 4827, 4839, 4851
                     , 4863, 4875, 4888, 4899, 4911, 4923, 4935, 4946, 4958, 4970
                     , 4983, 4995, 5008, 5020, 5032, 5045, 5056, 5068, 5080, 5093
                     , 5104, 5116, 5129, 5141, 5152, 5165, 5177, 5189, 5202, 5214
                     , 5226, 5237, 5249, 5262, 5275, 5287, 5298, 5311, 5324, 5335
                     , 5347, 5359, 5370, 5383, 5395, 5408, 5421, 5433, 5445, 5457
                     , 5469, 5481, 5492, 5505, 5518, 5529, 5542, 5555, 5567, 5579
                     , 5591, 5603, 5615, 5626, 5637, 5649, 5661, 5674, 5688, 5700
                     , 5711, 5723, 5736, 5748, 5760, 5771, 5783, 5796, 5808, 5820
                     , 5832, 5845, 5857, 5869, 5881, 5892, 5905, 5917, 5930, 5941
                     , 5955, 5967, 5978, 5990, 6002, 6014, 6027, 6039, 6050, 6062
                     , 6075, 6086, 6098, 6111, 6123, 6135, 6147, 6158, 6170, 6183
                     , 6195, 6208, 6220, 6232, 6242, 6254, 6265, 6277, 6289, 6301
                     , 6313, 6326, 6339, 6351, 6363, 6375, 6388, 6400, 6412, 6424
                     , 6435, 6446, 6458, 6470, 6482, 6493, 6505, 6517, 6528, 6540
                     , 6552, 6565, 6577, 6589, 6601, 6612, 6623, 6635, 6646, 6657
                     , 6667, 6679, 6690, 6700, 6710, 6719, 6729, 6744, 6753, 6766
                     , 6779

            })
    );
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestTigrPositionFileParser.class);
    
    @Test
    public void parse() throws IOException, DataStoreException{
        PositionDataStore actualMap = TigrPositionsFileParser.getPeakMap(RESOURCES.getFileAsStream(pathToPosfile));
        assertEquals(IWKNA07T07A12MP1027R.decode(), actualMap.get("IWKNA07T07A12MP1027R").decode());
        assertEquals(IWKNA07T08G07MP461F.decode(), actualMap.get("IWKNA07T08G07MP461F").decode());
        
    }
}
