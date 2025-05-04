package com.guhao;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jline.utils.InputStreamReader;
import org.slf4j.Logger;

import java.io.*;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static Logger LOGGER = LogUtils.getLogger();
    public static final ForgeConfigSpec.BooleanValue SLOW_TIME = BUILDER
            .comment("是否启用时间减缓效果")
            .define("slow_time", true);
    public static void Load(boolean isReload) {
        LOGGER.info("EpicAddon:Loading Sword Trail Item");
        LOGGER.info("EpicAddon:Loading Death Particle Modifier");


    }
    public static String ReadString(String FileName) {
        String str = "";
        File file = new File(FileName);
        IOException e;
        if (!file.exists()) {
            try {
                file.createNewFile();
                return "";
            } catch (IOException var4) {
                e = var4;
                throw new RuntimeException(e);
            }
        } else {
            try {
                str = readFromFile(FileName);
                return str;
            } catch (IOException var5) {
                e = var5;
                throw new RuntimeException(e);
            }
        }
    }


    public static void WriteString(String FileName, String str) {
        try {
            FileOutputStream fos = new FileOutputStream(FileName);

            try {
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                try {
                    BufferedWriter bw = new BufferedWriter(osw);

                    try {
                        bw.write(str);
                        bw.flush();
                    } catch (Throwable var10) {
                        try {
                            bw.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }

                        throw var10;
                    }

                    bw.close();
                } catch (Throwable var11) {
                    try {
                        osw.close();
                    } catch (Throwable var8) {
                        var11.addSuppressed(var8);
                    }

                    throw var11;
                }

                osw.close();
            } catch (Throwable var12) {
                try {
                    fos.close();
                } catch (Throwable var7) {
                    var12.addSuppressed(var7);
                }

                throw var12;
            }

            fos.close();
        } catch (FileNotFoundException var13) {
            FileNotFoundException e = var13;
            throw new RuntimeException(e);
        } catch (IOException var14) {
            IOException e = var14;
            throw new RuntimeException(e);
        }
    }

    public static String readFromFile(String s) throws IOException {
        InputStream inputStream = new FileInputStream(new File(s));
        StringBuilder resultStringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (Throwable var7) {
            try {
                br.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        br.close();
        return resultStringBuilder.toString();
    }
}