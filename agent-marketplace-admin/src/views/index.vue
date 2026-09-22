<template>
  <div class="app-container home">
    <section class="hero-card">
      <el-tag effect="plain" type="primary">平行数字官网</el-tag>
      <h1>官网运营概览</h1>
      <p>在这里统一维护官网菜单、新闻、资质证书与媒体素材，让每次内容更新都有清晰的发布路径。</p>
      <el-alert
        title="从左侧“企业官网”进入菜单管理、新闻管理、资质证书和媒体库。"
        type="info"
        :closable="false"
        show-icon
      />
    </section>

    <el-row :gutter="20" class="overview-grid">
      <el-col :xs="24" :sm="24" :lg="14">
        <el-card shadow="never" class="overview-card">
          <template #header>
            <div class="card-heading">
              <div>
                <span class="eyebrow">官网内容维护</span>
                <h2>内容维护入口</h2>
              </div>
              <span class="card-hint">请从左侧“企业官网”进入</span>
            </div>
          </template>

          <div class="entry-list">
            <div class="entry-item">
              <span class="entry-number">01</span>
              <div>
                <h3>菜单管理</h3>
                <p>维护官网页面与区块的展示顺序、导航层级和页面内容。</p>
              </div>
            </div>
            <div class="entry-item">
              <span class="entry-number">02</span>
              <div>
                <h3>新闻管理</h3>
                <p>新建、编辑和发布企业新闻，统一管理首页与新闻列表展示。</p>
              </div>
            </div>
            <div class="entry-item">
              <span class="entry-number">03</span>
              <div>
                <h3>资质证书</h3>
                <p>维护企业资质、荣誉证书及其在官网中的分类与展示信息。</p>
              </div>
            </div>
            <div class="entry-item">
              <span class="entry-number">04</span>
              <div>
                <h3>媒体库</h3>
                <p>集中上传和复用图片、视频与文件素材，便于内容引用和统一管理。</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="10">
        <el-card shadow="never" class="overview-card process-card">
          <template #header>
            <div class="card-heading">
              <div>
                <span class="eyebrow">内容发布流程</span>
                <h2>内容发布流程</h2>
              </div>
            </div>
          </template>

          <ol class="publish-flow">
            <li>
              <span>1</span>
              <div>
                <h3>准备内容与素材</h3>
                <p>先在对应栏目维护文字、图片和相关信息。</p>
              </div>
            </li>
            <li>
              <span>2</span>
              <div>
                <h3>保存草稿并核对</h3>
                <p>确认标题、摘要、图片和页面展示效果。</p>
              </div>
            </li>
            <li>
              <span>3</span>
              <div>
                <h3>发布到官网</h3>
                <p>发布后即可在官网前台查看最新内容。</p>
              </div>
            </li>
          </ol>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="overview-card tips-card">
      <template #header>
        <div class="card-heading">
          <div>
            <span class="eyebrow">运营提示</span>
            <h2>运营提示</h2>
          </div>
        </div>
      </template>
      <el-row :gutter="24">
        <el-col :xs="24" :sm="8">
          <div class="tip-item">
            <h3>先保存，再发布</h3>
            <p>内容可先以草稿形式保存，确认无误后再发布到官网。</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="tip-item">
            <h3>素材统一复用</h3>
            <p>建议先上传至媒体库，再从内容中引用，保持视觉素材一致。</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="tip-item">
            <h3>发布后检查前台</h3>
            <p>重要更新发布后，请在官网前台核对内容和链接是否展示正确。</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup name="Index">
</script>

<style scoped lang="scss">
.home {
  color: #253858;

  .hero-card {
    padding: 28px 32px;
    margin-bottom: 20px;
    background: linear-gradient(135deg, #f3f7ff 0%, #ffffff 72%);
    border: 1px solid #e4ecfb;
    border-radius: 10px;

    h1 {
      margin: 14px 0 10px;
      font-size: 28px;
      font-weight: 600;
      line-height: 1.35;
      color: #1d3557;
    }

    > p {
      max-width: 720px;
      margin: 0 0 20px;
      font-size: 14px;
      line-height: 1.8;
      color: #61708a;
    }
  }

  .overview-grid {
    margin-bottom: 20px;
  }

  .overview-card {
    height: 100%;
    border-color: #e8edf5;
    border-radius: 10px;

    :deep(.el-card__header) {
      padding: 18px 22px;
      border-bottom-color: #edf1f7;
    }

    :deep(.el-card__body) {
      padding: 22px;
    }
  }

  .card-heading {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;

    h2 {
      margin: 3px 0 0;
      font-size: 18px;
      font-weight: 600;
      color: #253858;
    }
  }

  .eyebrow {
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.08em;
    color: #7d8aa3;
  }

  .card-hint {
    flex: 0 0 auto;
    font-size: 12px;
    color: #8b97aa;
  }

  .entry-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .entry-item {
    display: flex;
    gap: 13px;
    min-height: 116px;
    padding: 18px;
    background: #fafcff;
    border: 1px solid #edf1f7;
    border-radius: 8px;

    .entry-number {
      flex: 0 0 auto;
      font-size: 13px;
      font-weight: 600;
      line-height: 1.8;
      color: #2d6cdf;
    }

    h3,
    p {
      margin: 0;
    }

    h3 {
      margin-bottom: 7px;
      font-size: 15px;
      font-weight: 600;
      color: #33435f;
    }

    p {
      font-size: 13px;
      line-height: 1.7;
      color: #748197;
    }
  }

  .publish-flow {
    padding: 0;
    margin: 0;
    list-style: none;

    li {
      display: flex;
      gap: 14px;
      padding: 2px 0 22px;

      &:last-child {
        padding-bottom: 0;
      }

      > span {
        display: inline-flex;
        flex: 0 0 28px;
        align-items: center;
        justify-content: center;
        width: 28px;
        height: 28px;
        font-size: 13px;
        font-weight: 600;
        color: #2d6cdf;
        background: #edf4ff;
        border-radius: 50%;
      }

      h3,
      p {
        margin: 0;
      }

      h3 {
        margin: 2px 0 6px;
        font-size: 15px;
        font-weight: 600;
        color: #33435f;
      }

      p {
        font-size: 13px;
        line-height: 1.7;
        color: #748197;
      }
    }
  }

  .tips-card {
    .tip-item {
      min-height: 88px;
      padding-left: 16px;
      border-left: 3px solid #d8e6ff;

      h3,
      p {
        margin: 0;
      }

      h3 {
        margin-bottom: 8px;
        font-size: 15px;
        font-weight: 600;
        color: #33435f;
      }

      p {
        font-size: 13px;
        line-height: 1.7;
        color: #748197;
      }
    }
  }

  @media (max-width: 767px) {
    .hero-card {
      padding: 22px 20px;

      h1 {
        font-size: 24px;
      }
    }

    .entry-list {
      grid-template-columns: 1fr;
    }

    .card-heading {
      align-items: flex-start;
      flex-direction: column;
      gap: 5px;
    }
  }
}
</style>
