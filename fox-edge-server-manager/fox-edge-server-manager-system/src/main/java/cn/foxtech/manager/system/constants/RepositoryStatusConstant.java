package cn.foxtech.manager.system.constants;

public class RepositoryStatusConstant {
    public static final int status_not_scanned = 0;
    public static final int status_not_downloaded = 1;
    public static final int status_downloaded = 2;
    public static final int status_decompressed = 3;
    public static final int status_not_installed = 4;
    public static final int status_installed = 5;

    // 破损状态：只是在查询阶段的计算状态，并像前面的0~5状态一样存储在内城区
    public static final int status_damaged_package = 6;
    // 升级状态：只是在查询阶段的计算状态，并像前面的0~5状态一样存储在内城区
    public static final int status_need_upgrade = 7;
}
