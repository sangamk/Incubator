package iccparser

import org.argus.amandroid.alir.componentSummary._
import org.argus.amandroid.core.decompile.{DecompileLayout, DecompileStrategy, DecompilerSettings}
import org.argus.amandroid.core.{AndroidGlobalConfig, ApkGlobal}
import org.argus.jawa.core.util._
import org.argus.jawa.core.{DefaultLibraryAPISummary, Reporter}
import writer.BaseGraphWriter

abstract class BaseAppParser() {

  val lineSeparator = "\r\n"
  var graph: List[(String, String, String)] = List()

  def loadApk(apkLocation: String, yard: ApkYard, reporter: Reporter): ApkGlobal = {
    val apkUri = FileUtil.toUri(apkLocation)
    val outputUri = FileUtil.toUri("./output")
    val layout = DecompileLayout(outputUri, createFolder = true, "src", "lib", createSeparateFolderForDexes = true)
    val strategy = DecompileStrategy(layout, new DefaultLibraryAPISummary(AndroidGlobalConfig.settings.third_party_lib_file))
    val settings = DecompilerSettings(debugMode = false, forceDelete = true, strategy, reporter)
    yard.loadApk(apkUri, settings, collectInfo = true, resolveCallBack = true, guessAppPackages = false)
  }

  def writeGraph(writers: Set[BaseGraphWriter], apk: ApkGlobal): Unit ={
    writers.foreach(w => w.write(graph, apk.model.getPackageName))
  }

  def parse(apk: ApkGlobal, yard: ApkYard)
  def collectIntents(apk: ApkGlobal)


//  def Intent(): DotGraphModel = {
//    val handler: AndroidModelCallHandler = new AndroidModelCallHandler
//    val sm: SummaryManager = new AndroidSummaryProvider(apk).getSummaryManager
//    val analysis = new BottomUpSummaryGenerator[Global](apk, sm, handler,
//      PTSummary(_, _),
//      ConsoleProgressBar.on(System.out).withFormat("[:bar] :percent% :elapsed Left: :remain"))
//    val store: PTStore = new PTStore
//    val sigs: ISet[Signature] = apk.model.getComponentInfos.flatMap(apk.getEntryPoints)
//    val cg = SignatureBasedCallGraph(apk, sigs, None)
//
//    val orderedWUs: IList[WorkUnit[Global]] = cg.topologicalSort(true).map { sig =>
//      val method = apk.getMethodOrResolve(sig).getOrElse(throw new RuntimeException("Method does not exist: " + sig))
//      new IntentWu(apk, method, sm, handler, store, "intent")
//    }
//
//    analysis.build(orderedWUs)
//    val candidate = store.getPropertyOrElse[MSet[(Context, PTASlot)]]("intent", msetEmpty)
//    val dotGraph = new DotGraphModel(cg)
//
//    candidate.foreach { case (ctx, s) =>
//      val intentInss = store.resolved.pointsToSet(ctx, s)
//      val intent = IntentHelper.getIntentContents(store.resolved, intentInss, ctx)
//      println(s"${ctx.getMethodSig.methodName} calls Intent:")
//      println(intent)
//      println()
//
//      if (intent.nonEmpty) {
//        if (intent.head.componentNames.nonEmpty) {
//          dotGraph.add(ctx.getMethodSig, intent, caller = true)
//        } else {
//          println(s"NO component link. Its likely an action ${intent.head}")
//        }
//      } else {
//        println(s"No intent found... $ctx")
//      }
//    }
//
//    dotGraph
//  }
//
//  def Spark(): CallGraph = {
//    val entryPoints = apk.getEntryPoints(AndroidConstants.MAINCOMP_ENV) // Exposed components
//    val spark = new InterProceduralSuperSpark(apk)
//    val idfg = spark.build(entryPoints.map(_.getSignature))
//    val icfg = idfg.icfg
//    return icfg.getCallGraph
//  }
//
//  def Signature(): CallGraph = {
//    val entryPoints = apk.getEntryPoints(AndroidConstants.MAINCOMP_ENV)
//    return SignatureBasedCallGraph(apk, entryPoints.map(_.getSignature), None)
//  }
}
