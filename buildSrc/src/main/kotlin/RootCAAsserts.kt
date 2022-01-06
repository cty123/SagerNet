import cn.hutool.core.text.csv.CsvReadConfig
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.crypto.digest.DigestUtil
import cn.hutool.http.HttpUtil
import org.gradle.api.Project
import java.io.File

fun Project.downloadRootCAList() {
    val assets = File(projectDir, "src/main/assets/root_ca_certs")
    assets.deleteRecursively()
    assets.mkdirs()
    val csv = HttpUtil.get("https://ccadb-public.secure.force.com/mozilla/IncludedCACertificateReportPEMCSV")
    val data = CsvUtil.getReader(CsvReadConfig().setContainsHeader(true)).readFromStr(csv)
    val list = mutableListOf<String>()
    for (row in data) {
        // skip China root CA
        if (row.getByName("Geographic Focus").contains("China")) continue
        val name = row.getByName("Common Name or Certificate Name")
        var cert = row.getByName("PEM Info")
        cert = cert.substring(1, cert.length - 1)
        File(assets, "$name.pem").writeText(cert)
        list.add(cert)
    }
    File(assets, "sha256sum").writeText(DigestUtil.sha256Hex(list.joinToString("\n")))
}