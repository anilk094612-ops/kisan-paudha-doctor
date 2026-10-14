package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdMobBannerView
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.HarvestAmber
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PaleGreenBg
import com.example.ui.viewmodel.PlantDoctorViewModel
import com.example.ui.viewmodel.Screen

data class DiseaseItem(
  val id: String,
  val nameHindi: String,
  val cropHindi: String,
  val type: String, // "रोग (Disease)", "कीट (Pest)", "पोषक तत्व (Nutrient)"
  val symptomsHindi: String,
  val preventionHindi: String,
  val organicControlHindi: String
)

val LIBRARY_DATA = listOf(
  // --- गन्ना (Sugarcane) की बीमारियां व कीट ---
  DiseaseItem(
    id = "s1",
    nameHindi = "लाल सड़न रोग (Red Rot of Sugarcane)",
    cropHindi = "गन्ना",
    type = "रोग (फफूंद)",
    symptomsHindi = "ऊपर की तीसरी और चौथी पत्तियां पीली पड़कर सूखने लगती हैं। गन्ने को चीरने पर भीतर का गूदा लाल दिखता है, बीच-बीच में सफेद आड़े धब्बे होते हैं और सिरके जैसी तीखी गंध आती है।",
    preventionHindi = "रोगग्रस्त गन्ने की पेड़ी (Ratoon) कभी न रखें। हमेशा प्रमाणित रोगरोधी बीज (सेट्स) का चुनाव करें। बुवाई से पूर्व बीजोपचार करें।",
    organicControlHindi = "संक्रमित पौधों को तुरंत जड़ सहित उखाड़कर खेत से बाहर जलाएं। ट्राइकोडर्मा 2-3 किग्रा प्रति एकड़ गोबर की खाद में मिलाकर खेत में डालें।"
  ),
  DiseaseItem(
    id = "s2",
    nameHindi = "कंसुआ / तना छेदक (Early Shoot Borer)",
    cropHindi = "गन्ना",
    type = "कीट (इल्ली)",
    symptomsHindi = "गन्ने के नवजात कल्ले सूख जाते हैं (डेड हार्ट बनता है) और खींचने पर आसानी से दुर्गंध के साथ बाहर निकल आते हैं।",
    preventionHindi = "बुवाई के 30-45 दिन बाद गन्ने की पंक्तियों पर मिट्टी चढ़ाएं (Earthing up)। सूखी पत्तियों की मल्चिंग करें ताकि कीट अंडे न दे सकें।",
    organicControlHindi = "ट्राइकोग्रामा किलोनिस (Trichogramma chilonis) परजीवी ततैया के 20,000 अंडे (कार्ड) प्रति एकड़ लगाएं। नीम का अर्क (5%) छिड़कें।"
  ),
  DiseaseItem(
    id = "s3",
    nameHindi = "पोक्का बोइंग व चोटी छेदक (Pokkah Boeng & Top Borer)",
    cropHindi = "गन्ना",
    type = "रोग व कीट",
    symptomsHindi = "गन्ने के ऊपरी नए पत्ते मुड़कर सिकुड़ जाते हैं, पत्तियों का आधार सफेद या लाल हो जाता है। चोटी छेदक से पत्तों में समानांतर गोल छेद (Shot holes) बनते हैं।",
    preventionHindi = "मानसून की पहली बारिश के बाद खेत में जलभराव न होने दें। संतुलित नाइट्रोजन दें।",
    organicControlHindi = "कॉपर ऑक्सीक्लोराइड 2.5 ग्राम प्रति लीटर या ट्राइकोडर्मा का छिड़काव करें। फेरोमोन ट्रैप लगाएं।"
  ),
  DiseaseItem(
    id = "s4",
    nameHindi = "पायरीला / गन्ने का फुदका (Sugarcane Pyrilla)",
    cropHindi = "गन्ना",
    type = "कीट (रस चूसक)",
    symptomsHindi = "पत्तियों की निचली सतह से वयस्कों व बच्चों द्वारा रस चूसने से पत्तियां पीली पड़ जाती हैं। पत्तियों पर मीठा चिपचिपा पदार्थ (मधुमक्खी मल) जमा होता है जिससे काली फफूंद (Sooty Mold) फैलती है।",
    preventionHindi = "गन्ने की निचली सूखी पत्तियों की छिलाई (Detrashing) करें ताकि कीट छिप न सकें।",
    organicControlHindi = "मित्र कीट एपिरिकैनिया मेलानोल्युका (Epiricania melanoleuca) का संरक्षण करें। 5% नीम तेल का छिड़काव करें।"
  ),

  // --- गेहूं (Wheat) की बीमारियां व कीट ---
  DiseaseItem(
    id = "w1",
    nameHindi = "पीला रतुआ (Yellow / Stripe Rust)",
    cropHindi = "गेहूं",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों पर पीले रंग की कतारबद्ध धारियों में हल्दी जैसा पाउडर उभरता है। पत्तियों को हाथ से छूने पर उंगलियों पर पीला पाउडर लग जाता है। पौधा कमजोर पड़ जाता है।",
    preventionHindi = "ओस के समय सुबह खेत में न घूमें ताकि बीजाणु न फैलें। रोगरोधी किस्में (जैसे HD 2967, DBW आदि) लगाएं। नाइट्रोजन यूरिया संतुलित दें।",
    organicControlHindi = "शुरुआती लक्षण पर खट्टी छाछ (मट्ठा) 50 मिली/लीटर या प्रोपिकोनाजोल (25% EC) 1 मिली/लीटर पानी में घोलकर छिड़कें।"
  ),
  DiseaseItem(
    id = "w2",
    nameHindi = "करनाल बंट व अनावृत कंडुआ (Karnal Bunt & Loose Smut)",
    cropHindi = "गेहूं",
    type = "रोग (फफूंद)",
    symptomsHindi = "बालियों के दानों की जगह काला चूर्ण भर जाता है। दानों को मसलने पर सड़ी हुई मछली जैसी दुर्गंध आती है। कंडुआ में पूरी बाली काली राख जैसी बन जाती है।",
    preventionHindi = "प्रमाणित बीज का ही उपयोग करें। बुवाई से पूर्व बीजोपचार कार्बेंडाजिम या ट्राइकोडर्मा (4 ग्राम/किग्रा बीज) से अवश्य करें।",
    organicControlHindi = "संक्रमित बालियों को पॉलीथिन की थैली से ढककर सावधानीपूर्वक तोड़ें और खेत से बाहर जलाएं।"
  ),
  DiseaseItem(
    id = "w3",
    nameHindi = "माहू / चेपा (Wheat Aphids)",
    cropHindi = "गेहूं",
    type = "कीट (रस चूसक)",
    symptomsHindi = "बादल छाए रहने पर बालियों और पत्तियों पर हरे-भूरे रंग के बारीक कीड़ों का झुंड रस चूसता है, जिससे दाने सिकुड़कर हल्के रह जाते हैं।",
    preventionHindi = "खेत के चारों ओर 10-12 पीले चिपचिपे ट्रैप (Yellow Sticky Traps) लगाएं। नाइट्रोजन की अधिक खुराक न दें।",
    organicControlHindi = "नीम का काढ़ा (5% NSKE) या नीम का तेल (1500 PPM) 3 मिली प्रति लीटर का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "w4",
    nameHindi = "दीमक व जड़ सड़न (Termites & Foot Rot)",
    cropHindi = "गेहूं",
    type = "कीट व मृदा रोग",
    symptomsHindi = "पौधे बीच-बीच में अचानक सूखने लगते हैं। खींचने पर पौधे बिना प्रयास जमीन से उखड़ जाते हैं और जड़ों पर बारीक सफेद/पीली दीमक दिखाई देती है।",
    preventionHindi = "खेत में कच्ची गोबर की खाद कभी न डालें (हमेशा अच्छी सड़ी कंपोस्ट ही डालें)।",
    organicControlHindi = "बवेरिया बैसियाना या मेटाराइजियम जैविक फफूंद 2 किग्रा प्रति एकड़ सिंचाई के पानी के साथ चलाएं।"
  ),

  // --- सरसों (Mustard) की बीमारियां व कीट ---
  DiseaseItem(
    id = "m1",
    nameHindi = "सफेद रोली / रतुआ (White Rust of Mustard)",
    cropHindi = "सरसों",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों की निचली सतह पर सफेद या हल्के पीले उभरे हुए छाले (Pustules) बनते हैं। फूल और फलियां विकृत होकर फूल जाती हैं (Staghead लक्षण)।",
    preventionHindi = "सरसों की अगेती बुवाई (अक्टूबर के पहले पखवाड़े) करें। संतुलित पोटाश व सल्फर का प्रयोग करें।",
    organicControlHindi = "लक्षण दिखते ही मेंकोजेब 2 ग्राम प्रति लीटर या ट्राइकोडर्मा 5 ग्राम प्रति लीटर पानी का छिड़काव करें। ग्रसित टहनियों को काटें।"
  ),
  DiseaseItem(
    id = "m2",
    nameHindi = "माहू / मोयला / चेपा (Mustard Aphids)",
    cropHindi = "सरसों",
    type = "कीट (रस चूसक)",
    symptomsHindi = "फूलों, फलियों और कोमल तनों पर लाखों हरे-काले माहू कीड़े चिपककर रस चूसते हैं। फलियों में दाने नहीं बनते और फसल चिपचिपी हो जाती है।",
    preventionHindi = "समय पर बुवाई करें। पीले चिपचिपे ट्रैप खेत में जगह-जगह लगाएं। लेडीबर्ड बीटल (मित्र कीट) का संरक्षण करें।",
    organicControlHindi = "साबुन का हल्का घोल (5 ग्राम डिटर्जेंट/लीटर) या नीम तेल (3000 PPM) 2 मिली/लीटर शाम को छिड़कें।"
  ),
  DiseaseItem(
    id = "m3",
    nameHindi = "अल्टरनेरिया पत्ती धब्बा व झुलसा (Alternaria Blight)",
    cropHindi = "सरसों",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों, तनों और फलियों पर गहरे कत्थई-काले छल्लेदार गोल धब्बे बनते हैं (Target Board लक्षण)। धब्बे आपस में मिलकर पत्तियों को झुलसा देते हैं।",
    preventionHindi = "स्वस्थ व उपचारित बीज ही बोएं। खेत में खरपतवार न पनपने दें।",
    organicControlHindi = "कॉपर ऑक्सीक्लोराइड 2.5 ग्राम प्रति लीटर पानी में मिलाकर 12-15 दिन के अंतराल पर 2 बार छिड़कें।"
  ),
  DiseaseItem(
    id = "m4",
    nameHindi = "तना गलन रोग (Sclerotinia Stem Rot)",
    cropHindi = "सरसों",
    type = "रोग (फफूंद)",
    symptomsHindi = "तने पर जमीन के पास सफेद रोईं जैसी फफूंद जमती है। तना अंदर से खोखला हो जाता है और उसमें चूहे की लेंडी जैसे काले कवक पिंड (Sclerotia) निकलते हैं।",
    preventionHindi = "गहरी जुताई करें। फसल चक्र अपनाएं (लगातार उसी खेत में सरसों न बोएं)। जलभराव से बचें।",
    organicControlHindi = "ट्राइकोडर्मा हरजियनम 2 किग्रा/एकड़ गोबर की खाद में मिलाकर मिट्टी में मिलाएं।"
  ),

  // --- धान (Paddy / Rice) की बीमारियां व कीट ---
  DiseaseItem(
    id = "p1",
    nameHindi = "शीथ ब्लाइट व खैरा रोग (Sheath Blight & Khaira)",
    cropHindi = "धान",
    type = "रोग (फफूंद व पोषण)",
    symptomsHindi = "पर्णच्छद (Sheath) पर सर्पिलाकार भूरे-हरे धब्बे बनते हैं। खैरा रोग में जिंक की कमी से पत्तियां कत्थई होकर जंग जैसी दिखने लगती हैं और पौधा बौना रह जाता है।",
    preventionHindi = "खेत से अतिरिक्त पानी निकालकर 3-4 दिन खेत को हवा लगने दें। सघन रोपाई से बचें।",
    organicControlHindi = "खैरा हेतु: जिंक सल्फेट (21%) 5 किग्रा + 2.5 किग्रा बुझा चूना 200 लीटर पानी में प्रति एकड़ छिड़कें। शीथ ब्लाइट हेतु ट्राइकोडर्मा दें।"
  ),
  DiseaseItem(
    id = "p2",
    nameHindi = "तना छेदक (Yellow Stem Borer)",
    cropHindi = "धान",
    type = "कीट (इल्ली)",
    symptomsHindi = "कल्ले निकलते समय पौधे की बीच की गोभ सूख जाती है (Dead Heart) और बाली आते समय बालियां सफेद व खोखली निकलती हैं (White Earhead)।",
    preventionHindi = "रोपाई के समय पौधों की पत्तियों के ऊपरी सिरे काटकर रोपें (अंडे नष्ट होते हैं)। प्रकाश प्रपंच (Light Trap) लगाएं।",
    organicControlHindi = "ट्राइकोग्रामा जपोनिकम परजीवी कार्ड 20,000 प्रति एकड़ लगाएं। नीम तेल 3 मिली/लीटर का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "p3",
    nameHindi = "भूरा फुदका / बीपीएच (Brown Plant Hopper - BPH)",
    cropHindi = "धान",
    type = "कीट (फुदका)",
    symptomsHindi = "पौधे के आधार पर पानी की सतह के पास भूरे फुदके रस चूसते हैं। खेत में जगह-जगह गोल घेरे में फसल जलकर सूख जाती है जिसे 'हॉपर बर्न' (Hopper Burn) कहते हैं।",
    preventionHindi = "खेत में हवा के लिए हर 8-10 कतार के बाद एक कतार खाली छोड़ें (Alleyways)। पानी बदलकर सुखाएं।",
    organicControlHindi = "खेत से पानी पूरी तरह खाली कर दें। नीम का काढ़ा पौधे के तने पर निशाना साधकर छिड़कें।"
  ),
  DiseaseItem(
    id = "p4",
    nameHindi = "ब्लास्ट रोग / झोंका रोग (Rice Blast Disease)",
    cropHindi = "धान",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों पर आंख या नाव के आकार के धब्बे बनते हैं जिनके किनारे भूरे और केंद्र राख जैसे सफेद होते हैं। गर्दन ब्लास्ट में बाली का निचला जोड़ काला होकर बाली टूटकर लटक जाती है।",
    preventionHindi = "यूरिया का अत्यधिक प्रयोग न करें। बीजोपचार करके ही रोपाई करें।",
    organicControlHindi = "स्यूडोमोनास फ्लोरेसेंस 5 ग्राम प्रति लीटर या ट्राइसाइक्लाजोल अनुशंसित मात्रा में छिड़कें।"
  ),
  DiseaseItem(
    id = "1",
    nameHindi = "पत्ती मरोड़िया रोग (Leaf Curl Virus)",
    cropHindi = "मिर्च, टमाटर",
    type = "रोग (वायरस)",
    symptomsHindi = "पत्ते ऊपर की ओर मुड़कर नाव जैसे बन जाते हैं, पौधे की बढ़वार रुक जाती है।",
    preventionHindi = "सफेद मक्खी पर नियंत्रण रखें, पीला चिपचिपा ट्रैप लगाएं।",
    organicControlHindi = "नीम का तेल (1500 PPM) 3 मिली/लीटर का 5 दिन के अंतराल पर 2 बार छिड़काव करें।"
  ),
  DiseaseItem(
    id = "2",
    nameHindi = "फल व तना छेदक इल्ली (Fruit & Shoot Borer)",
    cropHindi = "बैंगन, टमाटर",
    type = "कीट (इल्ली)",
    symptomsHindi = "मुलायम टहनियां सूखकर लटक जाती हैं और फल में बारीक छेद दिखता है जिसके बाहर बुरादा रहता है।",
    preventionHindi = "फेरोमोन ट्रैप 5 प्रति एकड़ लगाएं, ग्रसित टहनी काटकर नष्ट करें।",
    organicControlHindi = "नीम बीज अर्क (NSKE 5%) या बीटी (Bacillus thuringiensis) का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "3",
    nameHindi = "पीला शिरा मोज़ेक (Yellow Vein Mosaic)",
    cropHindi = "भिंडी",
    type = "रोग (वायरस)",
    symptomsHindi = "पत्तियों की नसें पहले पीली होती हैं, फिर पूरी पत्ती पीली हो जाती है।",
    preventionHindi = "रोगरोधी किस्में लगाएं, सफेद मक्खी को तुरंत रोकें।",
    organicControlHindi = "ग्रसित पौधों को तुरंत उखाड़कर जमीन में दबाएं। खट्टी छाछ और नीम तेल छिड़कें।"
  ),
  DiseaseItem(
    id = "4",
    nameHindi = "अगेती व पछेती झुलसा (Blight)",
    cropHindi = "टमाटर, आलू",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों पर भूरे-काले छल्लेदार धब्बे बनते हैं और पत्तियां झुलसी हुई दिखती हैं।",
    preventionHindi = "जड़ों के पास पानी न भरने दें, पौधों पर पानी का छिड़काव न करें।",
    organicControlHindi = "ट्राइकोडर्मा विरिडी 5 ग्राम प्रति लीटर पानी में मिलाकर छिड़कें।"
  ),
  DiseaseItem(
    id = "5",
    nameHindi = "चूर्णिल आसिता (Powdery Mildew)",
    cropHindi = "खीरा, लौकी, करेला",
    type = "रोग (फफूंद)",
    symptomsHindi = "पत्तियों की ऊपरी सतह पर सफेद आटे जैसा पाउडर दिखाई देता है।",
    preventionHindi = "खेत में वायु संचार अच्छा रखें, खरपतवार साफ करें।",
    organicControlHindi = "दूध का घोल (10% दूध पानी में) या बेकिंग सोडा 3 ग्राम प्रति लीटर का छिड़काव करें।"
  ),
  DiseaseItem(
    id = "6",
    nameHindi = "फूल झड़ना व फल मक्खी (Flower Drop & Fruit Fly)",
    cropHindi = "लौकी, तोरी, करेला",
    type = "कीट व परागण",
    symptomsHindi = "छोटे फल सड़कर गिर जाते हैं, फलों में डंक के निशान और मुड़ाव दिखता है।",
    preventionHindi = "मिथाइल यूजेनॉल ट्रैप लगाएं, शाम को हल्की नमी बनाए रखें।",
    organicControlHindi = "हस्त परागण करें और नीम तेल का नियमित छिड़काव करें।"
  )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseLibraryScreen(viewModel: PlantDoctorViewModel) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCropFilter by remember { mutableStateOf("सभी") }
  var expandedId by remember { mutableStateOf<String?>(null) }

  val filterChips = listOf("सभी", "गन्ना 🎋", "गेहूं 🌾", "सरसों 🌼", "धान 🌾", "टमाटर 🍅", "मिर्च 🌶️")

  val filteredList = LIBRARY_DATA.filter { item ->
    val matchesCrop = when (selectedCropFilter) {
      "सभी" -> true
      "गन्ना 🎋" -> item.cropHindi.contains("गन्ना")
      "गेहूं 🌾" -> item.cropHindi.contains("गेहूं")
      "सरसों 🌼" -> item.cropHindi.contains("सरसों")
      "धान 🌾" -> item.cropHindi.contains("धान")
      "टमाटर 🍅" -> item.cropHindi.contains("टमाटर")
      "मिर्च 🌶️" -> item.cropHindi.contains("मिर्च")
      else -> true
    }
    val matchesSearch = searchQuery.isBlank() ||
      item.nameHindi.contains(searchQuery, ignoreCase = true) ||
      item.cropHindi.contains(searchQuery, ignoreCase = true) ||
      item.type.contains(searchQuery, ignoreCase = true) ||
      item.symptomsHindi.contains(searchQuery, ignoreCase = true)

    matchesCrop && matchesSearch
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "रोग व कीट पहचान पुस्तिका",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.Home) },
            modifier = Modifier.testTag("back_library_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "मुख्य पृष्ठ",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = ForestGreen,
          titleContentColor = Color.White
        )
      )
    },
    bottomBar = {
      AdMobBannerView()
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(16.dp)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("बीमारी या फसल खोजें (उदा. गन्ना, गेहूं, सरसों, धान)") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForestGreen)
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "साफ करें")
            }
          }
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_search_disease")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Crop Filter Chips row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        filterChips.forEach { chipLabel ->
          val isSelected = selectedCropFilter == chipLabel
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) ForestGreen else PaleGreenBg,
            border = BorderStroke(1.dp, if (isSelected) ForestGreen else Color(0xFFA5D6A7)),
            modifier = Modifier
              .clickable { selectedCropFilter = chipLabel }
              .testTag("chip_filter_${chipLabel.take(4)}")
          ) {
            Text(
              text = chipLabel,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
              color = if (isSelected) Color.White else ForestGreen,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredList) { item ->
          val isExpanded = expandedId == item.id
          val isDark = isSystemInDarkTheme()
          Card(
            onClick = { expandedId = if (isExpanded) null else item.id },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isDark) Color(0xFF182418) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4CAF50) else Color(0xFFBCC6B8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.nameHindi,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else ForestGreen
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "फसल: ${item.cropHindi} • ${item.type}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFC8E6C9) else Color(0xFF111410)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isDark) Color(0xFF263C26) else PaleGreenBg,
                  border = BorderStroke(1.dp, if (isDark) Color(0xFF81C784) else Color(0xFF81C784))
                ) {
                  Text(
                    text = if (isExpanded) "कम देखें ▲" else "उपचार देखें ▼",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else ForestGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              if (isExpanded) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "👀 लक्षण:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFF81D4FA) else Color(0xFF01579B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.symptomsHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "🛡️ बचाव उपाय:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFF81C784) else ForestGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.preventionHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isDark) Color.White else Color(0xFF111410),
                  lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "🌿 जैविक / देसी उपचार:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = if (isDark) Color(0xFFFFB74D) else Color(0xFFB75500)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.organicControlHindi,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isDark) Color.White else Color(0xFF0A330A),
                  lineHeight = 22.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
